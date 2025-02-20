package com.honeyosori.dogfile.global.response.advice;

import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.global.response.dto.BaseResponse;
import com.honeyosori.dogfile.global.response.dto.CommonResponse;
import com.honeyosori.dogfile.global.response.dto.GlobalException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OAuthException.class)
    public BaseResponse<?> handleOAuthException(OAuthException e) {
        return BaseResponse.builder()
                .code(e.getStatus().getCode())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public CommonResponse handleException(Exception exception){
        if (exception instanceof GlobalException e) {
            return e.getStatus();
        }

        if(exception instanceof NoResourceFoundException) {
            return CommonResponse.INTERNAL_SERVER_ERROR;
        }

        if(exception instanceof HttpMessageNotReadableException) {
            return CommonResponse.REJECTED;
        }

        if(exception instanceof HttpRequestMethodNotSupportedException){
            return CommonResponse.REJECTED;
        }

        if(exception instanceof MissingRequestHeaderException){
            return CommonResponse.REJECTED;
        }

        return CommonResponse.INTERNAL_SERVER_ERROR;
    }
}