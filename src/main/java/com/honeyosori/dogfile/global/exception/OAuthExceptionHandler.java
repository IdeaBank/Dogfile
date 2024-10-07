package com.honeyosori.dogfile.global.exception;

import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OAuthExceptionHandler {
    @ExceptionHandler(OAuthException.class)
    protected ResponseEntity<?> handleOAuthException(OAuthException e) {
        return BaseResponse.getResponseEntity(new BaseResponse<>(e.getStatus(), null));
    }
}
