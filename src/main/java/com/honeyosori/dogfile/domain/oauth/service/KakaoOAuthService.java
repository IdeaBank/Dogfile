package com.honeyosori.dogfile.domain.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.honeyosori.dogfile.domain.oauth.component.KakaoTokenClient;
import com.honeyosori.dogfile.domain.oauth.component.KakaoUserInformationClient;
import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.domain.user.dto.CreateUserDto;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.constant.DogUrl;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

@Service
public class KakaoOAuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public KakaoOAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String getParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);

        if (value == null) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        return value;
    }

    private JsonObject convertToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String result;

        try {
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        return JsonParser.parseString(result).getAsJsonObject();
    }

    private String encodeUrl(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    public ResponseEntity<?> receiveKakaoCode(HttpServletRequest request) {
        String code = getParameter(request, "code");

        requestAccessToken(code);

        return ResponseEntity.ok().build();
    }

    private void requestAccessToken(String code) {
        KakaoTokenClient kakaoTokenClient = new KakaoTokenClient();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://kauth.kakao.com").build();

        JsonObject jsonObject = convertToJson(kakaoTokenClient);
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("redirect_uri", DogUrl.DOGGATE.getUrl() + "/dogfile" + "/v1/oauth/kakao_access_token");

        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .acceptCharset(StandardCharsets.UTF_8)
                .body(BodyInserters.fromValue(jsonObject))
                .retrieve();

        responseSpec.toBodilessEntity().block();
    }

    public ResponseEntity<?> receiveAccessToken(HttpServletRequest request) {
        String accessToken = getParameter(request, "access_token");
        String refreshToken = getParameter(request, "refresh_token");

        readUserInformation(accessToken);

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("access_token", accessToken);
        jsonObject.addProperty("refresh_token", refreshToken);

        return ResponseEntity.ok()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(jsonObject));
    }

    public void readUserInformation(String accessToken) {
        KakaoUserInformationClient kakaoUserInformationClient = new KakaoUserInformationClient();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://kauth.kakao.com").build();

        JsonObject kakaoUserInformationBody = convertToJson(kakaoUserInformationClient);
        kakaoUserInformationBody.addProperty("grant_type", "authorization_code");
        kakaoUserInformationBody.addProperty("access_token", accessToken);

        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri("/v2/user/me")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + accessToken)
                .acceptCharset(StandardCharsets.UTF_8)
                .body(BodyInserters.fromValue(kakaoUserInformationBody))
                .retrieve();

        String result = responseSpec.bodyToMono(String.class).block();

        if (result == null) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();

        if (jsonObject.isEmpty()) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        if (!jsonObject.has("kakao_account")) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        jsonObject = jsonObject.get("kakao_account").getAsJsonObject();

        if (!jsonObject.has("email")) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        String email = jsonObject.get("email").getAsString();

        User user = new User(email);

        this.userRepository.save(user);
    }

    private void sendRegisterRequestToDogus(String email, CreateKakaoAccountDto createKakaoAccountDto) {
        WebClient webClient = WebClient.builder()
                .baseUrl(DogUrl.DOGUS.getUrl())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonString = objectMapper.writeValueAsString(createKakaoAccountDto);

            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            jsonObject.addProperty("email", email);

            WebClient.ResponseSpec responseSpec = webClient.post()
                    .uri("/api/v1/user/register")
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(BodyInserters.fromValue(jsonObject))
                    .retrieve();
            String result = responseSpec.bodyToMono(String.class).block();

            System.out.println(result);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendRegisterRequestToDogchat(String userId, String email) {
        WebClient webClient = WebClient.builder()
                .baseUrl(DogUrl.DOGCHAT.getUrl())
                .build();

        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri(String.format("/api/v1/chat/user?userId=%s&userName=%s", userId, email))
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .retrieve();
        String result = responseSpec.bodyToMono(String.class).block();

        System.out.println(result);
    }

    public BaseResponse<?> registerNewAccount(String email, CreateKakaoAccountDto createKakaoAccountDto) {
        User user = this.userRepository.getUserByEmail(email);

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
