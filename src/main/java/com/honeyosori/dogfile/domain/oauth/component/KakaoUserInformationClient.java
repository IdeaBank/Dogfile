package com.honeyosori.dogfile.domain.oauth.component;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoUserInformationClient {
    private final String property_keys = "[\"kakao_account.email\"]";
}
