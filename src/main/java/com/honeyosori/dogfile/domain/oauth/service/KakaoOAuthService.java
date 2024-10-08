package com.honeyosori.dogfile.domain.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.honeyosori.dogfile.domain.oauth.component.KakaoOAuthClient;
import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import com.honeyosori.dogfile.domain.oauth.dto.KakaoUserInformation;
import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.constant.DogUrl;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoOAuthService {
    private final UserRepository userRepository;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public KakaoOAuthService(UserRepository userRepository, KakaoOAuthClient kakaoOAuthClient) {
        this.userRepository = userRepository;
        this.kakaoOAuthClient = kakaoOAuthClient;
    }

    private String encodeUrl(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        String code = request.getParameter("code");

        System.out.println(code);

        if (code == null) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        return requestAccessToken(code);
    }

    private ResponseEntity<?> requestAccessToken(String code) {
        RestClient restClient = RestClient.builder()
                .baseUrl(kakaoOAuthClient.getAuthUri()).build();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("grant_type", kakaoOAuthClient.getGrantType());
        requestBody.add("client_id", kakaoOAuthClient.getClientId());
        requestBody.add("code", code);
        requestBody.add("redirect_uri", kakaoOAuthClient.getRedirectUri() + "/dogfile/v1/oauth/kakao/oauth");

        System.out.println("Trying to receive access token");

        RestClient.ResponseSpec responseSpec = restClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .acceptCharset(StandardCharsets.UTF_8)
                .body(requestBody)
                .retrieve();

        String tokenResponse = responseSpec.onStatus(HttpStatusCode::isError, (request, response) -> {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }).body(String.class);

        JsonObject jsonObject = JsonParser.parseString(tokenResponse).getAsJsonObject();

        String accessToken = jsonObject.get("access_token").getAsString();
        String refreshToken = jsonObject.get("refresh_token").getAsString();

        Map<String, String> result = new HashMap<>();

        result.put("access_token", accessToken);
        result.put("refresh_token", refreshToken);

        readUserInformation(accessToken);

        return ResponseEntity.ok(result);
    }

    public void readUserInformation(String accessToken) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://kapi.kakao.com").build();

        MultiValueMap<String, String> kakaoUserInformationBody = new LinkedMultiValueMap<>();
        kakaoUserInformationBody.add("grant_type", "authorization_code");
        kakaoUserInformationBody.add("access_token", accessToken);

        RestClient.ResponseSpec responseSpec = restClient.post()
                .uri("/v2/user/me")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + accessToken)
                .acceptCharset(StandardCharsets.UTF_8)
                .body(kakaoUserInformationBody)
                .retrieve();

        KakaoUserInformation userInformationResult = responseSpec.onStatus(HttpStatusCode::isError, (request, response) -> {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }).body(KakaoUserInformation.class);

        String email = userInformationResult.getKakaoAccount().getEmail();
        boolean userExists = this.userRepository.findUserByEmail(email).isPresent();

        if (userExists) {
            return;
        }

        User user = new User(userInformationResult.getKakaoAccount().getEmail());
        this.userRepository.save(user);
    }

    private void sendRegisterRequestToDogus(String email, CreateKakaoAccountDto createKakaoAccountDto) {
        RestClient restClient = RestClient.builder()
                .baseUrl(DogUrl.DOGUS.getUrl())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonString = objectMapper.writeValueAsString(createKakaoAccountDto);

            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            jsonObject.addProperty("email", email);

            RestClient.ResponseSpec responseSpec = restClient.post()
                    .uri("/api/v1/user/register")
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(BodyInserters.fromValue(jsonObject))
                    .retrieve();

            String result = responseSpec.body(String.class);
            System.out.println(result);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendRegisterRequestToDogchat(String userId, String email) {
        RestClient restClient = RestClient.builder()
                .baseUrl(DogUrl.DOGCHAT.getUrl())
                .build();

        RestClient.ResponseSpec responseSpec = restClient.post()
                .uri(String.format("/api/v1/chat/user?userId=%s&userName=%s", userId, email))
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .retrieve();

        String result = responseSpec.body(String.class);
        System.out.println(result);
    }

    public BaseResponse<?> registerNewAccount(String email, CreateKakaoAccountDto createKakaoAccountDto) {
        User user = this.userRepository.getUserByEmail(email);

        if (user.getPassword() != null) {
            throw new OAuthException(BaseResponseStatus.USER_EXISTS);
        }

        user.setPassword(encoder.encode(createKakaoAccountDto.password()));
        user.setRealName(createKakaoAccountDto.realName());
        user.setGender(createKakaoAccountDto.gender());
        user.setBirthday(createKakaoAccountDto.birthday());
        user.setPhoneNumber(createKakaoAccountDto.phoneNumber());
        user.setAddress(createKakaoAccountDto.address());
        user.setProfileImageUrl(createKakaoAccountDto.profileImageUrl());

        this.userRepository.save(user);

        try {
            sendRegisterRequestToDogus(email, createKakaoAccountDto);
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
