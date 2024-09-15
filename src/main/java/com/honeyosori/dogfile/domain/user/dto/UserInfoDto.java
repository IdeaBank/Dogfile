package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;

import java.sql.Date;

public record UserInfoDto(String username,
                          String name,
                          String password,
                          String profileImageUrl,
                          Date birthday,
                          String phoneNumber,
                          String email) {
    public static UserInfoDto of(User user) {
        return new UserInfoDto(user.getUsername(), user.getRealName(),
                user.getPassword(), user.getProfileImageUrl(), user.getBirthday(),
                user.getPhoneNumber(), user.getEmail());
    }
}
