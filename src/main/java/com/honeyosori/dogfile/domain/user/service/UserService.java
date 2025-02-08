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
import org.springframework.transaction.annotation.Transactional;
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

    private void sendDeleteRequestToDogus(String dogfileUserId) {
        RestClient restClient = RestClient.builder()
                .baseUrl(DogUrl.DOGUS)
                .build();

        log.info("[DOGUS] Sending {}", dogfileUserId);

        String result = restClient.delete()
                .uri(DogUrl.DOGUS_DELETE + dogfileUserId)
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, (r, e) -> {
                    log.error("{} {}", e.getStatusCode(), e.getBody());
                    throw new OAuthException(BaseResponseStatus.INVALID_JWT_TOKEN);
                })
                .body(String.class);

        log.info("[DOGUS] Response {}", result);
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

    // TODO: SAGA 패턴 적용(보상 트랜잭션 적용)
    @Transactional
    public BaseResponse<?> register(CreateUserDto createUserDto) {
        String email = createUserDto.email();
        String profileImageUrl = "default";

        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user != null && user.getDeletedAt() == null) {
            return new BaseResponse<>(BaseResponseStatus.EMAIL_EXISTS, null);
        }

        else if (user != null) {
            return new BaseResponse<>(BaseResponseStatus.WITHDRAWN, null);
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
            sendDeleteRequestToDogus(newUser.getId());
            e.printStackTrace();
            throw new RuntimeException("DOGCLUB 등록 실패", e);
        }

        return new BaseResponse<>(BaseResponseStatus.CREATED, createUserDto);
    }

    @Transactional
    public BaseResponse<?> updateUser(UpdateUserDto updateUserDto, String email) {
        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        String oldAccountName = user.getAccountName();
        String oldProfileImageUrl = user.getProfileImageUrl();

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

        if (updateUserDto.phoneNumber() != null) {
            user.setPhoneNumber(updateUserDto.phoneNumber());
        }

        if (updateUserDto.email() != null) {
            user.setEmail(updateUserDto.email());
        }

        if (updateUserDto.accountName() != null || updateUserDto.profileImageUrl() != null) {
            try {
                UpdateDogusUserDto updateDogusUserDto = UpdateDogusUserDto.builder()
                        .accountName(updateUserDto.accountName())
                        .profileImageUrl(updateUserDto.profileImageUrl())
                        .build();

                log.info("[DOGUS] Send User Update Request");
                sendUpdateRequestToDogus(user.getId(), updateDogusUserDto);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("DOGUS 업데이트 실패", e);
            }

            try {
                UpdateDogclubUserDto updateDogclubUserDto = UpdateDogclubUserDto.builder()
                        .accountName(updateUserDto.accountName())
                        .profileImageUrl(updateUserDto.profileImageUrl())
                        .build();

                log.info("[DOGCLUB] Send User Update Request");
                sendUpdateRequestToDogclub(user.getId(), updateDogclubUserDto);
            } catch (Exception e) {
                UpdateDogusUserDto undoDogusUserDto = UpdateDogusUserDto.builder()
                        .accountName(oldAccountName)
                        .profileImageUrl(oldProfileImageUrl)
                        .build();

                sendUpdateRequestToDogus(user.getId(), undoDogusUserDto);
                e.printStackTrace();
                throw new RuntimeException("DOGCLUB 업데이트 실패", e);
            }
        }

        return new BaseResponse<>(BaseResponseStatus.UPDATED, updateUserDto);
    }

    private void sendUpdateRequestToDogclub(String dogfileUserId, UpdateDogclubUserDto updateDogclubUserDto) {
        RestClient restClient = RestClient.builder()
                .baseUrl(DogUrl.DOGUS)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonString = objectMapper.writeValueAsString(updateDogclubUserDto);
            log.info("[DOGCLUB] Sending {}", jsonString);

            String result = restClient.post()
                    .uri(DogUrl.DOGCLUB_UPDATE + dogfileUserId)
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

    private void sendUpdateRequestToDogus(String dogfileUserId, UpdateDogusUserDto updateDogusUserDto) {
        RestClient restClient = RestClient.builder()
                .baseUrl(DogUrl.DOGUS)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonString = objectMapper.writeValueAsString(updateDogusUserDto);
            log.info("[DOGUS] Sending {}", jsonString);

            String result = restClient.post()
                    .uri(DogUrl.DOGUS_UPDATE + dogfileUserId)
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

    public ResponseEntity<?> login(LoginDto loginDto) {
        String email = loginDto.email();
        String password = loginDto.password();

        User user = this.userRepository.findUserByEmailAndDeletedAtIsNull(email).orElse(null);

        if (user == null) {
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

    @Transactional(readOnly = true)
    public ResponseEntity<?> logout(String email) {
        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null) {
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
        User user = this.userRepository.getUserByEmailAndDeletedAtIsNull(email).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        user.setDeletedAt(LocalDateTime.now());

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
    }

    @Transactional
    public BaseResponse<?> cancelDeletion(String email) {
        User user = this.userRepository.getUserByEmailAndDeletedAtIsNotNull(email).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        user.setDeletedAt(null);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
    }

    @Transactional(readOnly = true)
    public BaseResponse<?> getWithdrawingUser() {
        List<User> deleted = this.userRepository.findByDeleted();

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, deleted);
    }

    @Transactional(readOnly = true)
    public BaseResponse<?> getUserInfo(String email) {
        User user = this.userRepository.findUserByEmailAndDeletedAtIsNull(email).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    @Transactional(readOnly = true)
    public BaseResponse<?> findAllUser(String email) {
        List<User> user = this.userRepository.findAllByEmailContainingAndDeletedAtIsNull(email);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, user.stream().map(UserInfoDto::of));
    }

    @Transactional(readOnly = true)
    public BaseResponse<?> getUserLoginInfo(String email) {
        User user = this.userRepository.findUserByEmailAndDeletedAtIsNull(email).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserLoginInfoDto userLoginInfoDto = UserLoginInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userLoginInfoDto);
    }

    @Transactional(readOnly = true)
    public BaseResponse<?> findUserById(String id) {
        User user = this.userRepository.findByIdAndDeletedAtIsNull(id).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    public BaseResponse<?> findUserByAccountName(String accountName) {
        User user = this.userRepository.findByAccountNameAndDeletedAtIsNull(accountName).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    public BaseResponse<?> findUserByPartialAccountName(String partialAccountName) {
        // TODO: Create Full-Text Index
        List<User> user = this.userRepository.findByAccountNameContainingAndDeletedAtIsNull(partialAccountName);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, user.stream().map(UserInfoDto::of));
    }

    public BaseResponse<?> findUserByEmail(String email) {
        User user = this.userRepository.findUserByEmailAndDeletedAtIsNull(email).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    public BaseResponse<?> findUserByPhoneNumber(String phoneNumber) {
        User user = this.userRepository.findByPhoneNumberAndDeletedAtIsNull(phoneNumber).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }
}
