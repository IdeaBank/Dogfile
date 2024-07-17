package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.global.constant.Role;
import lombok.Getter;

@Getter
public class UpdateUserDto {
    private String password;
    private Role role;
}
