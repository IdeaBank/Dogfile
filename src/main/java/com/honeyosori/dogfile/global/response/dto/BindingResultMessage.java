package com.honeyosori.dogfile.global.response.dto;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class BindingResultMessage {
    public static String of(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
