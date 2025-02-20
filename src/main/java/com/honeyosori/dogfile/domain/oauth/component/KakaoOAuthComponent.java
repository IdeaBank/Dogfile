package com.honeyosori.dogfile.domain.oauth.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoOAuthComponent {
    @Value("${oauth.kakao.api_key}")
    public String CLIENT_ID;

    @Value("${oauth.kakao.api_url}")
    public String API_URI;
}
