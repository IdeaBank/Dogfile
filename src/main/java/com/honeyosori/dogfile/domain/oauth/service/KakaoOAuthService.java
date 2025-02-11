package com.honeyosori.dogfile.domain.oauth.service;

import com.honeyosori.dogfile.domain.feign.client.DogclubClient;
import com.honeyosori.dogfile.domain.feign.client.DogusClient;
import com.honeyosori.dogfile.domain.oauth.component.KakaoOAuthComponent;
import com.honeyosori.dogfile.domain.oauth.constant.KakaoUrl;
import com.honeyosori.dogfile.domain.oauth.constant.TokenType;
import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import com.honeyosori.dogfile.domain.oauth.dto.KakaoUserInformation;
import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.domain.feign.dto.CreateDogclubUserDto;
import com.honeyosori.dogfile.domain.feign.dto.CreateDogusUserDto;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.constant.PayloadData;
import com.honeyosori.dogfile.global.response.dto.BaseResponse;
import com.honeyosori.dogfile.global.response.dto.GeneralResponse;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KakaoOAuthService {
    private final UserRepository userRepository;
    private final KakaoOAuthComponent kakaoOAuthComponent;
    private final JwtUtility jwtUtility;
    private final DogusClient dogusClient;
    private final DogclubClient dogclubClient;

    public KakaoOAuthService(
            UserRepository userRepository,
            KakaoOAuthComponent kakaoOAuthComponent,
            JwtUtility jwtUtility,
            DogusClient dogusClient,
            DogclubClient dogclubClient
    ) {
        this.userRepository = userRepository;
        this.kakaoOAuthComponent = kakaoOAuthComponent;
        this.jwtUtility = jwtUtility;
        this.dogusClient = dogusClient;
        this.dogclubClient = dogclubClient;
    }

    @Transactional
    public GeneralResponse registerUser(CreateKakaoAccountDto createKakaoAccountDto) {
        String email = getEmailUsingAccessToken(createKakaoAccountDto.kakaoAccessToken());
        Date birthday = createKakaoAccountDto.birthday();
        String phoneNumber = createKakaoAccountDto.phoneNumber();
        User.GenderType genderType = createKakaoAccountDto.gender();
        String realName = createKakaoAccountDto.realName();
        String accountName = createKakaoAccountDto.accountName();
        final String profileImageUrl = "default";

        this.userRepository.findUserByEmail(email).ifPresent((u) -> {
            throw new OAuthException(GeneralResponse.USER_EXISTS);
        });

        User user = new User(email, birthday, phoneNumber, genderType, realName, accountName);
        this.userRepository.save(user);

        CreateDogusUserDto createDogusUserDto = CreateDogusUserDto.builder()
                .dogfileUserId(user.getId())
                .accountName(createKakaoAccountDto.accountName())
                .profileImageUrl(profileImageUrl)
                .build();

        CreateDogclubUserDto createDogclubUserDto = CreateDogclubUserDto.builder()
                .dogfileUserId(user.getId())
                .accountName(createKakaoAccountDto.accountName())
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
            dogusClient.deleteUser(user.getId());
            throw new RuntimeException("DOGCLUB 등록 실패", e);
        }

        return GeneralResponse.CREATED;
    }

    public String getEmailUsingAccessToken(String accessToken) {
        RestClient restClient = RestClient.builder().baseUrl(kakaoOAuthComponent.API_URI).build();

        System.out.println("getting access token using " + accessToken);

        KakaoUserInformation kakaoUserInformation = restClient.get()
                .uri(KakaoUrl.GET_USER_INFORMATION)
                .header(HttpHeaders.AUTHORIZATION, TokenType.BEARER + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new OAuthException(GeneralResponse.REJECTED);
                })
                .body(KakaoUserInformation.class);

        if (kakaoUserInformation == null || kakaoUserInformation.getKakaoAccount() == null) {
            throw new OAuthException(GeneralResponse.INTERNAL_SERVER_ERROR);
        }

        if (kakaoUserInformation.getKakaoAccount().getEmail() == null || kakaoUserInformation.getKakaoAccount().getEmail().isEmpty()) {
            throw new OAuthException(GeneralResponse.INTERNAL_SERVER_ERROR);
        }

        return kakaoUserInformation.getKakaoAccount().getEmail();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> loginWithKakao(String accessToken) {
        accessToken = accessToken.replace("Bearer ", "");

        return readUserInformation(accessToken);
    }

    private ResponseEntity<?> readUserInformation(String kakaoAccessToken) {
        String email = getEmailUsingAccessToken(kakaoAccessToken);
        String dogusId;
        String dogclubId;

        // User 없으면 USER_NOT_FOUND 반환
        User user = this.userRepository.findUserByEmail(email).orElseThrow(()
                -> new OAuthException(GeneralResponse.USER_NOT_FOUND));

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

        Map<String, String> claims = new HashMap<>();
        claims.put(PayloadData.EMAIL, email);
        claims.put(PayloadData.DOGFILE, user.getId());
        claims.put(PayloadData.DOGUS, dogusId);
        claims.put(PayloadData.DOGCLUB, dogclubId);

        return jwtUtility.generateJwtResponse(claims);
    }
}
