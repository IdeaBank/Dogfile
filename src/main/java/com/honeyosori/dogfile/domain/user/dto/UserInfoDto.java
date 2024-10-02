package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;

import java.sql.Date;

public record UserInfoDto(String email,
                          String realName,
                          String profileImageUrl,
                          Date birthday,
                          String phoneNumber) {
    public static UserInfoDto of(User user) {
        return new UserInfoDto(user.getEmail(), user.getRealName(), user.getProfileImageUrl(),
                user.getBirthday(), user.getPhoneNumber());
    }
}
