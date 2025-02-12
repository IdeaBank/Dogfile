package com.honeyosori.dogfile.global.klat.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateKlatUserDto {
    private String userId;
    private String password;
    private String username;
    private String profileImageUrl;
}
