package com.honeyosori.dogfile.global.constant;

import lombok.Getter;
import org.springframework.http.HttpMethod;

public enum NonSecureMethod {
    REGISTER(HttpMethod.POST, "/v1/user"),
    LOGIN(HttpMethod.POST, "/v1/user/login"),
    PHOTO_UPLOAD(HttpMethod.POST, "/v1/photo"),
    HEALTH_CHECK(HttpMethod.GET, "/healthz"),
    ;

    @Getter
    private final HttpMethod httpMethod;
    @Getter
    private final String url;

    NonSecureMethod(HttpMethod httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
    }
}
