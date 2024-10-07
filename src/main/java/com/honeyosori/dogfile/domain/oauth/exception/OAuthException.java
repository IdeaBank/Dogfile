package com.honeyosori.dogfile.domain.oauth.exception;

import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import lombok.Getter;

@Getter
public class OAuthException extends RuntimeException {
    private BaseResponseStatus status;

    public OAuthException(BaseResponseStatus status) {
        this.status = status;
    }
}
