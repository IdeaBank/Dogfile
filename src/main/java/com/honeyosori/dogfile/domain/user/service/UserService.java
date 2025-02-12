package com.honeyosori.dogfile.domain.user.service;

import com.honeyosori.dogfile.domain.feign.client.DogclubClient;
import com.honeyosori.dogfile.domain.feign.client.DogusClient;
import com.honeyosori.dogfile.domain.feign.dto.CreateDogclubUserDto;
import com.honeyosori.dogfile.domain.feign.dto.CreateDogusUserDto;
import com.honeyosori.dogfile.domain.feign.dto.UpdateDogclubUserDto;
import com.honeyosori.dogfile.domain.feign.dto.UpdateDogusUserDto;
import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.entity.User.Role;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.constant.PayloadData;
import com.honeyosori.dogfile.global.exception.dto.GlobalException;
import com.honeyosori.dogfile.global.klat.component.KlatComponent;
import com.honeyosori.dogfile.global.klat.dto.CreateKlatUserDto;
import com.honeyosori.dogfile.global.klat.dto.KlatResponseDto;
import com.honeyosori.dogfile.global.klat.dto.LoginKlatDto;
import com.honeyosori.dogfile.global.response.dto.BaseResponse;
import com.honeyosori.dogfile.global.response.dto.GeneralResponse;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
    private final DogusClient dogusClient;
    private final DogclubClient dogclubClient;
    private final KlatComponent klatComponent;

    @Autowired
    public UserService(
            UserRepository userRepository,
            JwtUtility jwtUtility,
            RedisTemplate<String, String> redisTemplate,
            DogusClient dogusClient,
            DogclubClient dogclubClient,
            KlatComponent klatcomponent
    ) {
        this.userRepository = userRepository;
        this.jwtUtility = jwtUtility;
        this.redisTemplate = redisTemplate;
        this.dogusClient = dogusClient;
        this.dogclubClient = dogclubClient;
        this.klatComponent = klatcomponent;
    }

    @Transactional
    public CreateUserDto register(CreateUserDto createUserDto) {
        String email = createUserDto.email();
        String profileImageUrl = "default";

        this.userRepository.findUserByEmail(email)
                .ifPresent(u -> {
                    if (u.getDeletedAt() == null) {
                        throw new GlobalException(GeneralResponse.EMAIL_EXISTS);
                    } else {
                        throw new GlobalException(GeneralResponse.WITHDRAWN);
                    }
                });

        User newUser = createUserDto.toUser();
        newUser.setRole(Role.USER);
        newUser.setPassword(encoder.encode(newUser.getPassword()));

        this.userRepository.save(newUser);


        CreateDogusUserDto createDogusUserDto = CreateDogusUserDto.builder()
                .dogfileUserId(newUser.getId())
                .accountName(createUserDto.accountName())
                .profileImageUrl(profileImageUrl)
                .build();

        CreateDogclubUserDto createDogclubUserDto = CreateDogclubUserDto.builder()
                .dogfileUserId(newUser.getId())
                .accountName(createUserDto.accountName())
                .profileImageUrl(profileImageUrl)
                .build();

        CreateKlatUserDto createKlatUserDto = CreateKlatUserDto.builder()
                .userId(newUser.getId())
                .password(newUser.getId())
                .username(createUserDto.accountName())
                .profileImageUrl(profileImageUrl)
                .build();

        try {
            log.info("[DOGUS] Send User Create Request");
            ResponseEntity<?> dogusResponse = dogusClient.register(createDogusUserDto);
            log.info(dogusResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DOGUS 등록 실패", e);
        }

        try {
            log.info("[DOGCLUB] Send User Create Request");
            ResponseEntity<?> dogclubResponse = dogclubClient.register(createDogclubUserDto);
            log.info(dogclubResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dogusClient.deleteUser(newUser.getId());
            throw new RuntimeException("DOGCLUB 등록 실패", e);
        }

        try {
            log.info("[KLAT] Send User Create Request");
            RestClient restClient = RestClient.create();
            KlatResponseDto result = restClient.post()
                    .uri("https://api.talkplus.io/v1.4/api/users/create")
                    .headers(headers -> {
                        headers.set("app-id", klatComponent.getKLAT_APP_ID());
                        headers.set("api-key", klatComponent.getKLAT_API_KEY());
                    })
                    .body(createKlatUserDto)
                    .retrieve()
                    .body(KlatResponseDto.class);
            log.info("[KLAT] " + result.getLoginToken());
        } catch (Exception e) {
            throw new RuntimeException("KLAT 등록 실패", e);
        }

        return createUserDto;
    }

    @Transactional
    public UpdateUserDto updateUser(UpdateUserDto updateUserDto, String email) {
        User user = this.userRepository.findUserByEmail(email)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

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
            UpdateDogusUserDto updateDogusUserDto = UpdateDogusUserDto.builder()
                    .accountName(updateUserDto.accountName())
                    .profileImageUrl(updateUserDto.profileImageUrl())
                    .build();

            UpdateDogclubUserDto updateDogclubUserDto = UpdateDogclubUserDto.builder()
                    .accountName(updateUserDto.accountName())
                    .profileImageUrl(updateUserDto.profileImageUrl())
                    .build();

            try {
                log.info("[DOGUS] Send User Update Request");
                ResponseEntity<?> dogusResponse = dogusClient.updateUser(user.getId(), updateDogusUserDto);
                log.info(dogusResponse.toString());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("DOGUS 업데이트 실패", e);
            }

            try {
                log.info("[DOGCLUB] Send User Update Request");
                ResponseEntity<?> dogclubResponse = dogclubClient.updateUser(user.getId(), updateDogclubUserDto);
                log.info(dogclubResponse.toString());
            } catch (Exception e) {
                e.printStackTrace();

                UpdateDogusUserDto undoUpdateUserDto = UpdateDogusUserDto.builder()
                        .accountName(oldAccountName)
                        .profileImageUrl(oldProfileImageUrl)
                        .build();

                dogusClient.updateUser(user.getId(), undoUpdateUserDto);
                throw new RuntimeException("DOGCLUB 업데이트 실패", e);
            }
        }

        return updateUserDto;
    }

    public ResponseEntity<?> login(LoginDto loginDto) {
        String email = loginDto.email();
        String password = loginDto.password();
        String dogusId;
        String dogclubId;
        String klatLoginToken;

        User user = this.userRepository.findUserByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

        try {
            log.info("[DOGUS] Send User Find Request");
            dogusId = dogusClient.findByDogfileUserId(user.getId());
            log.info(dogusId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DOGUS 찾기 실패", e);
        }

        try {
            log.info("[DOGCLUB] Send User Find Request");
            dogclubId = dogclubClient.findByDogfileUserId(user.getId());
            log.info(dogclubId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DOGCLUB 찾기 실패", e);
        }

        LoginKlatDto loginKlatDto = LoginKlatDto.builder()
                .userId(user.getId())
                .password(user.getId())
                .build();

        try {
            log.info("[KLAT] Send User Create Request");
            RestClient restClient = RestClient.create();
            KlatResponseDto result = restClient.post()
                    .uri("https://api.talkplus.io/v1.4/api/users/login")
                    .headers(headers -> {
                        headers.set("app-id", klatComponent.getKLAT_APP_ID());
                        headers.set("api-key", klatComponent.getKLAT_API_KEY());
                    })
                    .body(loginKlatDto)
                    .retrieve()
                    .body(KlatResponseDto.class);
            log.info("[KLAT] " + result.getLoginToken());
            klatLoginToken = result.getLoginToken();
        } catch (Exception e) {
            throw new RuntimeException("KLAT 등록 실패", e);
        }

        if (encoder.matches(password, user.getPassword())) {
            Map<String, String> claims = new HashMap<>();

            claims.put(PayloadData.EMAIL, email);
            claims.put(PayloadData.DOGFILE, user.getId());
            claims.put(PayloadData.DOGUS, dogusId);
            claims.put(PayloadData.DOGCLUB, dogclubId);
            claims.put(PayloadData.KLAT, klatLoginToken);

            return jwtUtility.generateJwtResponse(claims);
        }

        throw new GlobalException(GeneralResponse.WRONG_PASSWORD);
    }

    @Transactional(readOnly = true)
    public GeneralResponse logout(String email) {
        User user = this.userRepository.findUserByEmail(email)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, "");

        return GeneralResponse.SUCCESS;
    }

    public ResponseEntity<?> refresh(String email, String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String whitelistToken = valueOperations.get(email);

        if (Objects.equals(whitelistToken, "")) {
            throw new GlobalException(GeneralResponse.UNAUTHENTICATED);
        } else if (!Objects.equals(whitelistToken, refreshToken)) {
            throw new GlobalException(GeneralResponse.EXPIRED_JWT_TOKEN);
        }

        Map<String, String> claims = new HashMap<>();

        claims.put(PayloadData.EMAIL, email);

        return jwtUtility.generateJwtResponse(claims);
    }

    @Transactional
    public GeneralResponse deleteUser(String email) {
        User user = this.userRepository.getUserByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

        user.setDeletedAt(LocalDateTime.now());

        return GeneralResponse.SUCCESS;
    }

    @Transactional
    public GeneralResponse cancelDeletion(String email) {
        User user = this.userRepository.getUserByEmailAndDeletedAtIsNotNull(email)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

        user.setDeletedAt(null);

        return GeneralResponse.SUCCESS;
    }

    @Transactional(readOnly = true)
    public List<UserInfoDto> getWithdrawingUser() {
        List<User> user = this.userRepository.findByDeleted();

        return user.stream().map(UserInfoDto::of).toList();
    }

    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo(String email) {
        User user = this.userRepository.findUserByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

        return UserInfoDto.of(user);
    }

    @Transactional(readOnly = true)
    public List<UserInfoDto> findAllUser(String email) {
        List<User> user = this.userRepository.findAllByEmailContainingAndDeletedAtIsNull(email);

        return user.stream().map(UserInfoDto::of).toList();
    }

    @Transactional(readOnly = true)
    public UserLoginInfoDto getUserLoginInfo(String email) {
        User user = this.userRepository.findUserByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

        return UserLoginInfoDto.of(user);
    }

    @Transactional(readOnly = true)
    public UserInfoDto findUserById(String id) {
        User user = this.userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

        return UserInfoDto.of(user);
    }

    public UserInfoDto findUserByAccountName(String accountName) {
        User user = this.userRepository.findByAccountNameAndDeletedAtIsNull(accountName)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

        return UserInfoDto.of(user);
    }

    public List<UserInfoDto> findUserByPartialAccountName(String partialAccountName) {
        // TODO: Create Full-Text Index
        List<User> user = this.userRepository.findByAccountNameContainingAndDeletedAtIsNull(partialAccountName);

        return user.stream().map(UserInfoDto::of).toList();
    }

    public UserInfoDto findUserByEmail(String email) {
        User user = this.userRepository.findUserByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

        return UserInfoDto.of(user);
    }

    public UserInfoDto findUserByPhoneNumber(String phoneNumber) {
        User user = this.userRepository.findByPhoneNumberAndDeletedAtIsNull(phoneNumber)
                .orElseThrow(() -> new GlobalException(GeneralResponse.USER_NOT_FOUND));

        return UserInfoDto.of(user);
    }
}
