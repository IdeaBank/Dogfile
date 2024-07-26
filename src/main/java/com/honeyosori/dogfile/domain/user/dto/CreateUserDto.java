package com.honeyosori.dogfile.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.global.constant.Role;
import com.honeyosori.dogfile.global.constant.UserStatus;

import java.sql.Date;

public record CreateUserDto(String username,
                            String name,
                            @JsonIgnore String password,
                            String profileImageUrl,
                            Date birthday,
                            String phoneNumber,
                            String email,
                            Role role,
                            UserStatus userStatus) {
    public User toUser() {
        return new User(username, name, password, profileImageUrl, birthday, phoneNumber, email, role, userStatus);
    }
}
