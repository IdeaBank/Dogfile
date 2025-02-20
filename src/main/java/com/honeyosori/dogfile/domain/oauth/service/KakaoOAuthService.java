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
import com.honeyosori.dogfile.domain.klat.component.KlatComponent;
import com.honeyosori.dogfile.domain.klat.dto.CreateKlatUserDto;
import com.honeyosori.dogfile.domain.klat.dto.KlatResponseDto;
import com.honeyosori.dogfile.domain.klat.dto.KlatLoginDto;
import com.honeyosori.dogfile.global.response.dto.CommonResponse;
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
    private final KlatComponent klatComponent;
    private final RestClient restClient;

    public KakaoOAuthService(
            UserRepository userRepository,
            KakaoOAuthComponent kakaoOAuthComponent,
            JwtUtility jwtUtility,
            DogusClient dogusClient,
            DogclubClient dogclubClient,
            KlatComponent klatcomponent,
            RestClient restClient
    ) {
        this.userRepository = userRepository;
        this.kakaoOAuthComponent = kakaoOAuthComponent;
        this.jwtUtility = jwtUtility;
        this.dogusClient = dogusClient;
        this.dogclubClient = dogclubClient;
        this.klatComponent = klatcomponent;
        this.restClient = restClient;
    }

    @Transactional
    public CommonResponse registerUser(CreateKakaoAccountDto createKakaoAccountDto) {
        String email = getEmailUsingAccessToken(createKakaoAccountDto.kakaoAccessToken());
        Date birthday = createKakaoAccountDto.birthday();
        String phoneNumber = createKakaoAccountDto.phoneNumber();
        User.GenderType genderType = createKakaoAccountDto.gender();
        String realName = createKakaoAccountDto.realName();
        String accountName = createKakaoAccountDto.accountName();
        final String profileImageUrl = "default";

        this.userRepository.findUserByEmail(email).ifPresent((u) -> {
            throw new OAuthException(CommonResponse.USER_EXISTS);
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

        CreateKlatUserDto createKlatUserDto = CreateKlatUserDto.builder()
                .userId(user.getId())
                .password(user.getId())
                .username(createKakaoAccountDto.accountName())
                .profileImageUrl(profileImageUrl)
                .build();


        try {
            log.info("[DOGUS] Send User Create Request");
            //ResponseEntity<?> dogusResponse = dogusClient.register(createDogusUserDto);
            //log.info(dogusResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAuthException(CommonResponse.INTERNAL_SERVER_ERROR, "DOGUS 등록 실패");
        }

        try {
            log.info("[DOGCLUB] Send User Create Request");
            //ResponseEntity<?> dogclubResponse = dogclubClient.register(createDogclubUserDto);
            //log.info(dogclubResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dogusClient.deleteUser(user.getId());
            throw new RuntimeException("DOGCLUB 등록 실패", e);
        }

        try {
            log.info("[KLAT] Send User Create Request");
            KlatResponseDto result = restClient.post()
                    .uri("https://api.talkplus.io/v1.4/api/users/create")
                    .headers(headers -> {
                        headers.set("app-id", klatComponent.getKLAT_APP_ID());
                        headers.set("api-key", klatComponent.getKLAT_API_KEY());
                    })
                    .body(createKlatUserDto)
                    .retrieve()
                    .body(KlatResponseDto.class);
            log.info("[KLAT] " + result.loginToken());
        } catch (Exception e) {
            throw new RuntimeException("KLAT 등록 실패", e);
        }

        return CommonResponse.CREATED;
    }

    public String getEmailUsingAccessToken(String accessToken) {
        RestClient restClient = RestClient.builder().baseUrl(kakaoOAuthComponent.API_URI).build();

        System.out.println("getting access token using " + accessToken);

        KakaoUserInformation kakaoUserInformation = restClient.get()
                .uri(KakaoUrl.GET_USER_INFORMATION)
                .header(HttpHeaders.AUTHORIZATION, TokenType.BEARER + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new OAuthException(CommonResponse.REJECTED);
                })
                .body(KakaoUserInformation.class);

        if (kakaoUserInformation == null || kakaoUserInformation.getKakaoAccount() == null) {
            throw new OAuthException(CommonResponse.INTERNAL_SERVER_ERROR);
        }

        if (kakaoUserInformation.getKakaoAccount().getEmail() == null || kakaoUserInformation.getKakaoAccount().getEmail().isEmpty()) {
            throw new OAuthException(CommonResponse.INTERNAL_SERVER_ERROR);
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
        String klatLoginToken;

        // User 없으면 USER_NOT_FOUND 반환
        User user = this.userRepository.findUserByEmail(email).orElseThrow(()
                -> new OAuthException(CommonResponse.USER_NOT_FOUND));

        try {
            log.info("[DOGUS] Send User Find Request");
            //dogusId = dogusClient.findByDogfileUserId(user.getId());
            //log.info(dogusId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DOGUS 찾기 실패", e);
        }

        try {
            log.info("[DOGCLUB] Send User Find Request");
            //dogclubId = dogclubClient.findByDogfileUserId(user.getId());
            //log.info(dogclubId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DOGCLUB 찾기 실패", e);
        }

        KlatLoginDto klatLoginDto = KlatLoginDto.builder()
                .userId(user.getId())
                .password(user.getId())
                .build();

        try {
            log.info("[KLAT] Send User Create Request");
            klatLoginToken = tryKlatLogin(klatLoginDto).loginToken();
            log.info("[KLAT] " + klatLoginToken);
        } catch (Exception e) {
            throw new RuntimeException("KLAT 등록 실패", e);
        }

        Map<String, String> claims = new HashMap<>();
        claims.put(PayloadData.EMAIL, email);
        claims.put(PayloadData.DOGFILE, user.getId());
        //claims.put(PayloadData.DOGUS, dogusId);
        //claims.put(PayloadData.DOGCLUB, dogclubId);
        claims.put(PayloadData.KLAT, klatLoginToken);

        return jwtUtility.generateJwtResponse(claims);
    }

    private KlatResponseDto tryKlatLogin(KlatLoginDto klatLoginDto) {
        return restClient.post()
                .uri("https://api.talkplus.io/v1.4/api/users/login")
                .headers(headers -> {
                    headers.set("app-id", klatComponent.getKLAT_APP_ID());
                    headers.set("api-key", klatComponent.getKLAT_API_KEY());
                })
                .body(klatLoginDto)
                .retrieve()
                .body(KlatResponseDto.class);
    }
}
