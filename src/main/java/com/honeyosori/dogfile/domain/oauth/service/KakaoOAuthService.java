package com.honeyosori.dogfile.domain.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyosori.dogfile.domain.oauth.component.KakaoOAuthComponent;
import com.honeyosori.dogfile.domain.oauth.constant.KakaoUrl;
import com.honeyosori.dogfile.domain.oauth.constant.TokenType;
import com.honeyosori.dogfile.domain.oauth.dto.CreateDogusAccountDto;
import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import com.honeyosori.dogfile.domain.oauth.dto.KakaoTokenResponse;
import com.honeyosori.dogfile.domain.oauth.dto.KakaoUserInformation;
import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.constant.*;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
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

    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        String code = request.getParameter(RequestParameter.CODE);

        if (code == null) {
            throw new OAuthException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

        return requestAccessToken(code);
    }

    public String getEmailUsingAccessToken(String accessToken) {
        RestClient restClient = RestClient.builder().baseUrl(kakaoOAuthComponent.API_URI).build();

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
                }).body(KakaoTokenResponse.class);

        if (tokenResponse == null) {
            throw new OAuthException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

        return readUserInformation(tokenResponse.getAccessToken());
    }

    public ResponseEntity<?> readUserInformation(String kakaoAccessToken) {
        String email = getEmailUsingAccessToken(kakaoAccessToken);

        User user = this.userRepository.findUserByEmail(email).orElse(null);

        if (user != null && user.getPassword() != null) {
            Map<String, String> claims = new HashMap<>();

            claims.put(PayloadData.ORIGIN, JwtOrigin.LOCAL.getName());
            claims.put(PayloadData.EMAIL, email);

            return jwtUtility.generateJwtResponse(claims);
        }

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

    private void sendRegisterRequestToDogus(CreateDogusAccountDto createDogusAccountDto) {
        RestClient restClient = RestClient.builder().baseUrl(DogUrl.DOGUS).build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String dogusAccountInformation = objectMapper.writeValueAsString(createDogusAccountDto);

            RestClient.ResponseSpec responseSpec = restClient.post().uri(DogUrl.DOGUS_REGISTER).headers(httpHeaders -> {
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            }).body(BodyInserters.fromValue(dogusAccountInformation)).retrieve();

            String result = responseSpec.body(String.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendRegisterRequestToDogchat(String userId, String email) {
        RestClient restClient = RestClient.builder().baseUrl(DogUrl.DOGCHAT).build();

        RestClient.ResponseSpec responseSpec = restClient.post().uri(String.format(DogUrl.DOGCHAT_REGISTER, userId, email)).headers(httpHeaders -> {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }).retrieve();

        String result = responseSpec.body(String.class);
        System.out.println(result);
    }

    public BaseResponse<?> registerKakaoAccount(String email, CreateKakaoAccountDto createKakaoAccountDto) {
        User user = this.userRepository.getUserByEmail(email);

        if (user.getPassword() != null) {
            throw new OAuthException(BaseResponseStatus.USER_EXISTS);
        }

        user.setPassword(encoder.encode(createKakaoAccountDto.password()));
        user.registerKakaoUser(createKakaoAccountDto);

        this.userRepository.save(user);

        try {
            CreateDogusAccountDto createDogusAccountDto = createKakaoAccountDto.toDogusAccount(email);
            sendRegisterRequestToDogus(createDogusAccountDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sendRegisterRequestToDogchat(user.getId(), email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new BaseResponse<>(BaseResponseStatus.CREATED, createKakaoAccountDto);
    }
}
