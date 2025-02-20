package com.honeyosori.dogfile.global.response.dto;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    private final CommonResponse status;

    public GlobalException(CommonResponse status) {
        this.status = status;
    }
}