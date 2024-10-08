package com.honeyosori.dogfile.global.exception;

import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import com.honeyosori.dogfile.global.response.BindingResultMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OAuthException.class)
    private ResponseEntity<?> handleOAuthException(OAuthException e) {
        return BaseResponse.getResponseEntity(e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<?> methodValidException(MethodArgumentNotValidException e) {
        List<String> errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(BindingResultMessage::of).toList();

        return BaseResponse.getResponseEntity(new BaseResponse<>(BaseResponseStatus.REJECTED, errorMessage));
    }
}
