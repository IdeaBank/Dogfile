package com.honeyosori.dogfile.domain.oauth.exception;

import com.honeyosori.dogfile.global.response.dto.BaseResponse;
import com.honeyosori.dogfile.global.response.dto.GeneralResponse;
import lombok.Getter;

@Getter
public class OAuthException extends RuntimeException {
    private final GeneralResponse status;

    public OAuthException(GeneralResponse status) {
        super(status.getMessage());
        this.status = status;
    }
}
