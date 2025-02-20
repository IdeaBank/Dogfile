package com.honeyosori.dogfile.global.response.advice;

import com.honeyosori.dogfile.global.response.dto.GlobalException;
import com.honeyosori.dogfile.global.response.dto.BindingResultMessage;
import com.honeyosori.dogfile.global.response.dto.CommonResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.apache.http.entity.ContentType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class BindingExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<?> methodValidException(MethodArgumentNotValidException e) {
        List<String> errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(BindingResultMessage::of).toList();

        return ResponseEntity.badRequest().header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()).body(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    private CommonResponse globalException(Exception e) {
        e.printStackTrace();

        if (e instanceof GlobalException globalException) {
            return globalException.getStatus();
        }

        return CommonResponse.INTERNAL_SERVER_ERROR;
    }
}
