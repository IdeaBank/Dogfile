package com.honeyosori.dogfile.global.exception.dto;

import com.honeyosori.dogfile.global.response.dto.GeneralResponse;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    private final GeneralResponse status;

    public GlobalException(GeneralResponse status) {
        this.status = status;
    }
}