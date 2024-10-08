package com.honeyosori.dogfile.domain.oauth.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoOAuthClient {
    @Value("${oauth.kakao.api_key}")
    private String clientId;

    @Value("${oauth.kakao.auth_uri}")
    private String authUri;

    @Value("${oauth.kakao.api_uri}")
    private String apiUri;

    @Value("${oauth.kakao.redirect_uri}")
    private String redirectUri;

    private final String grantType = "authorization_code";
}
