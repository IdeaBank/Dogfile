package com.honeyosori.dogfile.domain.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyosori.dogfile.domain.oauth.component.KakaoOAuthComponent;
import com.honeyosori.dogfile.domain.oauth.constant.KakaoUrl;
import com.honeyosori.dogfile.domain.oauth.constant.TokenType;
import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import com.honeyosori.dogfile.domain.oauth.dto.KakaoTokenResponse;
import com.honeyosori.dogfile.domain.oauth.dto.KakaoUserInformation;
import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.domain.user.dto.CreateDogclubUserDto;
import com.honeyosori.dogfile.domain.user.dto.CreateDogusUserDto;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.constant.*;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KakaoOAuthService {
    private final UserRepository userRepository;
    private final KakaoOAuthComponent kakaoOAuthComponent;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtility jwtUtility;

    public KakaoOAuthService(UserRepository userRepository, KakaoOAuthComponent kakaoOAuthComponent, JwtUtility jwtUtility) {
        this.userRepository = userRepository;
        this.kakaoOAuthComponent = kakaoOAuthComponent;
        this.jwtUtility = jwtUtility;
    }

    // 카카오서버에 auth 인증을 받고, 유저를 임시로 저장한다음, 다음 register 요청으로 빠졌던 유저의 정보를 채우는거?
    @Transactional
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        String code = request.getParameter(RequestParameter.CODE);

        if (code == null) {
            throw new OAuthException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

        return requestAccessToken(code);
    }

    public String getEmailUsingAccessToken(String accessToken) {
        RestClient restClient = RestClient.builder().baseUrl(kakaoOAuthComponent.API_URI).build();

        System.out.println("getting access token using " + accessToken);

        KakaoUserInformation kakaoUserInformation = restClient.get()
                .uri(KakaoUrl.GET_USER_INFORMATION)
                .header(HttpHeaders.AUTHORIZATION, TokenType.BEARER + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new OAuthException(BaseResponseStatus.REJECTED);
                })
                .body(KakaoUserInformation.class);

        if (kakaoUserInformation == null || kakaoUserInformation.getKakaoAccount() == null) {
            throw new OAuthException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

        if (kakaoUserInformation.getKakaoAccount().getEmail() == null || kakaoUserInformation.getKakaoAccount().getEmail().isEmpty()) {
            throw new OAuthException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

        System.out.println(kakaoUserInformation.getKakaoAccount().getEmail());

        return kakaoUserInformation.getKakaoAccount().getEmail();
    }

    private ResponseEntity<?> requestAccessToken(String code) {
        RestClient restClient = RestClient.builder().baseUrl(kakaoOAuthComponent.AUTH_URI).build();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        requestBody.add(RequestParameter.CODE, code);
        requestBody.add(RequestParameter.GRANT_TYPE, kakaoOAuthComponent.GRANT_TYPE);
        requestBody.add(RequestParameter.CLIENT_ID, kakaoOAuthComponent.CLIENT_ID);
        requestBody.add(RequestParameter.REDIRECT_URI, kakaoOAuthComponent.REDIRECT_URI + DogUrl.DOGFILE_OAUTH);

        KakaoTokenResponse tokenResponse = restClient.post()
                .uri(KakaoUrl.GET_TOKEN)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .acceptCharset(StandardCharsets.UTF_8)
                .body(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new OAuthException(BaseResponseStatus.REJECTED);
                })
                .body(KakaoTokenResponse.class);

        if (tokenResponse == null) {
            throw new OAuthException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

        return readUserInformation(tokenResponse.getAccessToken());
    }

    @Transactional
    public ResponseEntity<?> loginWithKakao(String accessToken) {
        accessToken = accessToken.replace("Bearer ", "");

        return readUserInformation(accessToken);
    }

    private ResponseEntity<?> readUserInformation(String kakaoAccessToken) {
        String email = getEmailUsingAccessToken(kakaoAccessToken);

        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user != null && user.getPassword() != null) {
            Map<String, String> claims = new HashMap<>();

            claims.put(PayloadData.ORIGIN, JwtOrigin.LOCAL.getName());
            claims.put(PayloadData.EMAIL, email);

            return jwtUtility.generateJwtResponse(claims);
        }

        // 이해가 안되는 코드, nullable false면 에러나지 않나?
        if (user == null) {
            User newUser = new User(email);
            this.userRepository.save(newUser);
        }

        Map<String, String> claims = new HashMap<>();

        claims.put(PayloadData.ORIGIN, JwtOrigin.KAKAO.getName());
        claims.put(PayloadData.EMAIL, email);
        claims.put(PayloadData.ACCESS_TOKEN, kakaoAccessToken);

        return jwtUtility.generateJwtResponse(claims);
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
        RestClient restClient = RestClient.builder().baseUrl(DogUrl.DOGUS).build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String dogusAccountInformation = objectMapper.writeValueAsString(createDogusUserDto);

            log.info("[DOGUS] Sending {}", dogusAccountInformation);

            String result = restClient.post()
                    .uri(DogUrl.DOGUS_REGISTER)
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(dogusAccountInformation)
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

    private void sendRegisterRequestToDogclub(CreateDogclubUserDto createDogclubUserDto) {
        RestClient restClient = RestClient.builder().baseUrl(DogUrl.DOGCLUB).build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String dogclubAccountInformation = objectMapper.writeValueAsString(createDogclubUserDto);

            log.info("[DOGCLUB] Sending {}", dogclubAccountInformation);

            String result = restClient.post()
                    .uri(DogUrl.DOGCLUB_REGISTER)
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(dogclubAccountInformation)
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
    public BaseResponse<?> registerKakaoAccount(String email, CreateKakaoAccountDto createKakaoAccountDto) {
        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user == null) {
            throw new OAuthException(BaseResponseStatus.USER_NOT_FOUND);
        }
        else if (user.getDeletedAt() != null) {
            throw new OAuthException(BaseResponseStatus.WITHDRAWN);
        }
        else if (user.getPassword() != null) {
            throw new OAuthException(BaseResponseStatus.USER_EXISTS);
        }

        user.setPassword(encoder.encode(createKakaoAccountDto.password()));
        user.registerKakaoUser(createKakaoAccountDto);

        this.userRepository.save(user);

        try {
            CreateDogusUserDto createDogusUserDto = CreateDogusUserDto.builder()
                    .dogfileUserId(user.getId())
                    .accountName(email)
                    .profileImageUrl(createKakaoAccountDto.profileImageUrl())
                    .build();

            log.info("[DOGUS] Send User Create Request");
            sendRegisterRequestToDogus(createDogusUserDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DOGUS 등록 실패", e);
        }

        try {
            CreateDogclubUserDto createDogclubUserDto = CreateDogclubUserDto.builder()
                    .dogfileUserId(user.getId())
                    .accountName(email)
                    .profileImageUrl(createKakaoAccountDto.profileImageUrl())
                    .build();

            log.info("[DOGCLUB] Send User Create Request");
            sendRegisterRequestToDogclub(createDogclubUserDto);
        } catch (Exception e) {
            sendDeleteRequestToDogus(user.getId());
            e.printStackTrace();
            throw new RuntimeException("DOGCLUB 등록 실패", e);
        }

        return new BaseResponse<>(BaseResponseStatus.CREATED, createKakaoAccountDto);
    }
}
