package com.honeyosori.dogfile.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.entity.User.Role;
import com.honeyosori.dogfile.domain.user.entity.User.UserStatus;
import com.honeyosori.dogfile.domain.user.entity.WithdrawWaiting;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.domain.user.repository.WithdrawWaitingRepository;
import com.honeyosori.dogfile.global.constant.DogUrl;
import com.honeyosori.dogfile.global.constant.JwtOrigin;
import com.honeyosori.dogfile.global.constant.PayloadData;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;
    private final WithdrawWaitingRepository withdrawWaitingRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtility jwtUtility;

    @Autowired
    public UserService(UserRepository userRepository, WithdrawWaitingRepository withdrawWaitingRepository, JwtUtility jwtUtility) {
        this.userRepository = userRepository;
        this.withdrawWaitingRepository = withdrawWaitingRepository;
        this.jwtUtility = jwtUtility;
    }

    private void sendWithdrawRequestToDogchat(String userId) {
        WebClient webClient = WebClient.builder()
                .baseUrl(DogUrl.DOGCHAT)
                .build();

        WebClient.ResponseSpec responseSpec = webClient.delete()
                .uri(String.format(DogUrl.DOGCHAT_WITHDRAW, userId))
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .retrieve();

        String result = responseSpec.bodyToMono(String.class).block();
    }

    private void sendRegisterRequestToDogus(CreateUserDto createUserDto) {
        RestClient restClient = RestClient.builder()
                .baseUrl(DogUrl.DOGUS)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonString = objectMapper.writeValueAsString(createUserDto);

            RestClient.ResponseSpec responseSpec = restClient.post()
                    .uri(DogUrl.DOGUS_REGISTER)
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(BodyInserters.fromValue(jsonString))
                    .retrieve();

            String result = responseSpec.onStatus(HttpStatusCode::is4xxClientError, (r, e) -> {
                        throw new OAuthException(BaseResponseStatus.INVALID_JWT_TOKEN);
                    })
                    .body(String.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendRegisterRequestToDogchat(String userId, String email) {
        WebClient webClient = WebClient.builder()
                .baseUrl(DogUrl.DOGCHAT)
                .build();

        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri(String.format(DogUrl.DOGCHAT_REGISTER, userId, email))
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .retrieve();

        String result = responseSpec.bodyToMono(String.class).block();
    }

    public BaseResponse<?> register(CreateUserDto createUserDto) {
        String email = createUserDto.email();

        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user != null) {
            return new BaseResponse<>(BaseResponseStatus.EMAIL_EXISTS, null);
        }

        User newUser = createUserDto.toUser();
        newUser.setRole(Role.USER);
        newUser.setUserStatus(UserStatus.PUBLIC);
        newUser.setPassword(encoder.encode(newUser.getPassword()));

        this.userRepository.save(newUser);

        try {
            sendRegisterRequestToDogus(createUserDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sendRegisterRequestToDogchat(newUser.getId(), email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new BaseResponse<>(BaseResponseStatus.CREATED, createUserDto);
    }

    public BaseResponse<?> updateUser(UpdateUserDto updateUserDto, String email) {
        User user = this.userRepository.getUserByEmail(email);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.INVALID_JWT_TOKEN, null);
        }

        if (updateUserDto.password() != null) {
            user.setPassword(encoder.encode(updateUserDto.password()));
        }

        if (updateUserDto.profileImageUrl() != null) {
            user.setProfileImageUrl(updateUserDto.profileImageUrl());
        }

        if (updateUserDto.birthday() != null) {
            user.setBirthday(updateUserDto.birthday());
        }

        if (updateUserDto.phoneNumber() != null) {
            user.setPhoneNumber(updateUserDto.phoneNumber());
        }

        this.userRepository.save(user);

        return new BaseResponse<>(BaseResponseStatus.UPDATED, updateUserDto);
    }

    public BaseResponse<?> changeUserStatus(UpdateUserStatusDto updateUserStatusDto, String email) {
        User user = this.userRepository.getUserByEmail(email);
        UserStatus userStatus = updateUserStatusDto.userStatus();

        if (userStatus == UserStatus.PUBLIC || userStatus == UserStatus.PRIVATE) {
            user.setUserStatus(userStatus);
            this.userRepository.save(user);

            return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
        }

        return new BaseResponse<>(BaseResponseStatus.REJECTED, "cannot assign value");
    }

    public ResponseEntity<?> login(LoginDto loginDto) {
        String email = loginDto.email();
        String password = loginDto.password();

        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null) {
            return BaseResponse.getResponseEntity(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (user.getUserStatus() == UserStatus.WITHDRAW_REQUESTED) {
            return BaseResponse.getResponseEntity(BaseResponseStatus.WITHDRAW_REQUESTED);
        }

        if (user.getUserStatus() == UserStatus.WITHDRAWN) {
            return BaseResponse.getResponseEntity(BaseResponseStatus.WITHDRAWN);
        }

        if (encoder.matches(password, user.getPassword())) {
            Map<String, String> claims = new HashMap<>();

            claims.put(PayloadData.ORIGIN, JwtOrigin.LOCAL.getName());
            claims.put(PayloadData.EMAIL, email);

            return jwtUtility.generateJwtResponse(claims);
        }

        return BaseResponse.getResponseEntity(BaseResponseStatus.WRONG_PASSWORD);
    }

    public void deleteUser(String email) {
        User user = this.userRepository.getUserByEmail(email);

        user.resetUser();
        user.setUserStatus(UserStatus.WITHDRAWN);

        this.userRepository.save(user);
    }

    public BaseResponse<?> processWithdrawRequest(String email) {
        User user = this.userRepository.getUserByEmail(email);

        if (this.withdrawWaitingRepository.existsByUserId(user.getId())) {
            return new BaseResponse<>(BaseResponseStatus.ALREADY_WAITING_FOR_WITHDRAW, null);
        }

        WithdrawWaiting withdrawWaiting = new WithdrawWaiting(user);
        this.withdrawWaitingRepository.save(withdrawWaiting);

        user.setUserStatus(UserStatus.WITHDRAW_REQUESTED);
        this.userRepository.save(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
    }

    public BaseResponse<?> getUserInfo(String email) {
        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    public BaseResponse<?> getUserLoginInfo(String email) {
        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserLoginInfoDto userLoginInfoDto = UserLoginInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userLoginInfoDto);
    }
}
