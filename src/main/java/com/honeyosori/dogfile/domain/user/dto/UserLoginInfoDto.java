package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;

public record UserLoginInfoDto(String email) {
    public static UserLoginInfoDto of(User user) {
        return new UserLoginInfoDto(user.getEmail());
    }
}
