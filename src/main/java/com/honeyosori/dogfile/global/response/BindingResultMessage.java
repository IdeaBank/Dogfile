package com.honeyosori.dogfile.global.response;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.List;

@Component
public class BindingResultMessage {
    public static String of(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
