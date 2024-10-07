package com.honeyosori.dogfile.domain.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.honeyosori.dogfile.domain.oauth.component.KakaoTokenClient;
import com.honeyosori.dogfile.domain.oauth.component.KakaoUserInformationClient;
import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.global.constant.DogUrl;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.yaml.snakeyaml.util.UriEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class KakaoOAuthService {
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
        } catch(JsonProcessingException e) {
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

        if(result == null) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();

        if(jsonObject.isEmpty()) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        if(!jsonObject.has("kakao_account")) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        jsonObject = jsonObject.get("kakao_account").getAsJsonObject();

        if(!jsonObject.has("email")) {
            throw new OAuthException(BaseResponseStatus.REJECTED);
        }

        String email = jsonObject.get("email").getAsString();

        System.out.println(email);

        return ResponseEntity.ok().build();
    }
}
