package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.global.constant.Role;
import com.honeyosori.dogfile.global.constant.UserStatus;

import java.sql.Date;

public record UserInfoDto(String username,
                          String name,
                          String profileImageUrl,
                          Date birthday,
                          String phoneNumber,
                          String email,
                          Role role,
                          UserStatus userStatus) {
    public static UserInfoDto of(User user) {
        return new UserInfoDto(user.getUsername(), user.getRealName(),
                user.getProfileImageUrl(), user.getBirthday(),
                user.getPhoneNumber(), user.getEmail(),
                user.getRole(), user.getUserStatus());
    }
}
