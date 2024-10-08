package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;

import java.sql.Date;

public record UserInfoDto(String id,
                          String email,
                          String profileImageUrl,
                          Date birthday,
                          String phoneNumber) {
    public static UserInfoDto of(User user) {
        return new UserInfoDto(user.getId(), user.getEmail(), user.getProfileImageUrl(),
                user.getBirthday(), user.getPhoneNumber());
    }
}
