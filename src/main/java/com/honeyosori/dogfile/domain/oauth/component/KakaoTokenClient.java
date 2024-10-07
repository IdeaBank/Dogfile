package com.honeyosori.dogfile.domain.oauth.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoTokenClient {
    @Value("${oauth.kakao.api_key}")
    protected String client_id;

    private final String grant_type = "authorization_code";
}
