package com.honeyosori.dogfile.domain.oauth.exception;

import com.honeyosori.dogfile.global.response.dto.CommonResponse;
import lombok.Getter;

@Getter
public class OAuthException extends RuntimeException {
    private final CommonResponse status;

    public OAuthException(CommonResponse status) {
        super(status.getMessage());
        this.status = status;
    }

    public OAuthException(CommonResponse status, String message) {
        super(message);
        this.status = status;
    }
}
