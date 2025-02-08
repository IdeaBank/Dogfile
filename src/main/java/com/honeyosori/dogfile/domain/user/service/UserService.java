package com.honeyosori.dogfile.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.entity.User.Role;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.constant.DogUrl;
import com.honeyosori.dogfile.global.constant.JwtOrigin;
import com.honeyosori.dogfile.global.constant.PayloadData;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Transactional
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtility jwtUtility;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtility jwtUtility, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.jwtUtility = jwtUtility;
        this.redisTemplate = redisTemplate;
    }

    private void sendRegisterRequestToDogus(CreateDogusUserDto createDogusUserDto) {
        RestClient restClient = RestClient.builder()
                .baseUrl(DogUrl.DOGUS)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonString = objectMapper.writeValueAsString(createDogusUserDto);
            log.info("[DOGUS] Sending {}", jsonString);

            String result = restClient.post()
                    .uri(DogUrl.DOGUS_REGISTER)
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(jsonString)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (r, e) -> {
                        log.error("{} {}", e.getStatusCode(), e.getBody());
                        throw new OAuthException(BaseResponseStatus.INVALID_JWT_TOKEN);
                    })
                    .body(String.class);

            log.info("[DOGUS] Response {}", result);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendRegisterRequestToDogclub(CreateDogclubUserDto createDogclubUserDto) {
        RestClient restClient = RestClient.builder()
                .baseUrl(DogUrl.DOGCLUB)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonString = objectMapper.writeValueAsString(createDogclubUserDto);
            log.info("[DOGCLUB] Sending {}", jsonString);

            String result = restClient.post()
                    .uri(DogUrl.DOGCLUB_REGISTER)
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(jsonString)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (r, e) -> {
                        log.error("{} {}", e.getStatusCode(), e.getBody());
                        throw new OAuthException(BaseResponseStatus.INVALID_JWT_TOKEN);
                    })
                    .body(String.class);

            log.info("[DOGCLUB] Response {}", result);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // TODO: 트랜잭션 롤백, SAGA 패턴 적용(보상 트랜잭션 적용)
    @Transactional
    public BaseResponse<?> register(CreateUserDto createUserDto) {
        String email = createUserDto.email();
        String profileImageUrl = "default";

        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user != null) {
            return new BaseResponse<>(BaseResponseStatus.EMAIL_EXISTS, null);
        }

        User newUser = createUserDto.toUser();
        newUser.setRole(Role.USER);
        newUser.setPassword(encoder.encode(newUser.getPassword()));

        this.userRepository.save(newUser);

        try {
            CreateDogusUserDto createDogusUserDto = CreateDogusUserDto.builder()
                    .dogfileUserId(newUser.getId())
                    .accountName(createUserDto.accountName())
                    .profileImageUrl(profileImageUrl)
                    .build();

            log.info("[DOGUS] Send User Create Request");
            sendRegisterRequestToDogus(createDogusUserDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DOGUS 등록 실패", e);
        }

        try {
            CreateDogclubUserDto createDogclubUserDto = CreateDogclubUserDto.builder()
                    .dogfileUserId(newUser.getId())
                    .accountName(createUserDto.accountName())
                    .profileImageUrl(profileImageUrl)
                    .build();

            log.info("[DOGCLUB] Send User Create Request");
            sendRegisterRequestToDogclub(createDogclubUserDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DOGCLUB 등록 실패", e);
        }

        return new BaseResponse<>(BaseResponseStatus.CREATED, createUserDto);
    }

    @Transactional
    public BaseResponse<?> updateUser(UpdateUserDto updateUserDto, String email) {
        // TODO: Create DB Index of email
        User user = this.userRepository.getUserByEmail(email);

        if (user == null || user.getDeleted()) {
            return new BaseResponse<>(BaseResponseStatus.INVALID_JWT_TOKEN, null);
        }

        if (updateUserDto.accountName() != null) {
            user.setAccountName(updateUserDto.accountName());
        }

        if (updateUserDto.password() != null) {
            user.setPassword(encoder.encode(updateUserDto.password()));
        }

        if (updateUserDto.realName() != null) {
            user.setRealName(updateUserDto.realName());
        }

        if (updateUserDto.gender() != null) {
            user.setGender(updateUserDto.gender());
        }

        if (updateUserDto.birthday() != null) {
            user.setBirthday(updateUserDto.birthday());
        }

        if (updateUserDto.profileImageUrl() != null) {
            user.setProfileImageUrl(updateUserDto.profileImageUrl());
        }

        if (updateUserDto.email() != null) {
            user.setEmail(updateUserDto.email());
        }

        return new BaseResponse<>(BaseResponseStatus.UPDATED, updateUserDto);
    }

    public ResponseEntity<?> login(LoginDto loginDto) {
        String email = loginDto.email();
        String password = loginDto.password();

        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null || user.getDeleted()) {
            return BaseResponse.getResponseEntity(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (encoder.matches(password, user.getPassword())) {
            Map<String, String> claims = new HashMap<>();

            claims.put(PayloadData.ORIGIN, JwtOrigin.LOCAL.getName());
            claims.put(PayloadData.EMAIL, email);

            return jwtUtility.generateJwtResponse(claims);
        }

        return BaseResponse.getResponseEntity(BaseResponseStatus.WRONG_PASSWORD);
    }

    public ResponseEntity<?> logout(String email) {
        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null || user.getDeleted()) {
            return BaseResponse.getResponseEntity(BaseResponseStatus.USER_NOT_FOUND);
        }

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, "");

        return BaseResponse.getResponseEntity(BaseResponseStatus.SUCCESS);
    }

    public ResponseEntity<?> refresh(String email, String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String whitelistToken = valueOperations.get(email);

        if (Objects.equals(whitelistToken, "")) {
            return BaseResponse.getResponseEntity(BaseResponseStatus.UNAUTHENTICATED);
        } else if (!Objects.equals(whitelistToken, refreshToken)) {
            return BaseResponse.getResponseEntity(BaseResponseStatus.EXPIRED_JWT_TOKEN);
        }

        Map<String, String> claims = new HashMap<>();

        claims.put(PayloadData.ORIGIN, JwtOrigin.LOCAL.getName());
        claims.put(PayloadData.EMAIL, email);

        return jwtUtility.generateJwtResponse(claims);
    }

    @Transactional
    public BaseResponse<?> deleteUser(String email) {
        User user = this.userRepository.getUserByEmail(email);

        user.setDeleted(true);

        user.setWithdrawRequestAt(LocalDateTime.now());

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
    }

    @Transactional
    public BaseResponse<?> cancelDeletion(String email) {
        User user = this.userRepository.getUserByEmail(email);

        user.setDeleted(false);
        user.setWithdrawRequestAt(null);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
    }

    public BaseResponse<?> getWithdrawingUser() {
        // TODO: Create DB Index of Deleted
        List<User> user = this.userRepository.findByDeleted();

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, user);
    }

    public BaseResponse<?> getUserInfo(String email) {
        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null || user.getDeleted()) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    public BaseResponse<?> findAllUser(String email) {
        List<User> user = this.userRepository.findAllByEmailContaining(email);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, user.stream().map(UserInfoDto::of));
    }

    public BaseResponse<?> getUserLoginInfo(String email) {
        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null || user.getDeleted()) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserLoginInfoDto userLoginInfoDto = UserLoginInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userLoginInfoDto);
    }

    public BaseResponse<?> findUserById(String id) {
        User user = this.userRepository.findById(id).orElse(null);

        if (user == null || user.getDeleted()) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    public BaseResponse<?> findUserByAccountName(String accountName) {
        // TODO: Create DB Index of Account Name
        User user = this.userRepository.findByAccountName(accountName).orElse(null);

        if (user == null || user.getDeleted()) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    public BaseResponse<?> findUserByPartialAccountName(String partialAccountName) {
        // Only starting with like can use a normal index
        // TODO: Create Full-Text Index
        List<User> user = this.userRepository.findByAccountNameStartingWith(partialAccountName).orElse(null);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, user.stream().map(UserInfoDto::of));
    }

    public BaseResponse<?> findUserByEmail(String email) {
        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null || user.getDeleted()) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    public BaseResponse<?> findUserByPhoneNumber(String phoneNumber) {
        User user = this.userRepository.findByPhoneNumber(phoneNumber).orElse(null);

        if (user == null || user.getDeleted()) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }
}
