package com.honeyosori.dogfile.global.klat.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginKlatDto {
    private String userId;
    private String password;
}
