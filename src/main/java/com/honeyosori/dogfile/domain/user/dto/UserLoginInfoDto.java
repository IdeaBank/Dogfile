package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;

public record UserLoginInfoDto(String username) {
    public static UserLoginInfoDto of(User user) {
        return new UserLoginInfoDto(user.getUsername());
    }
}
