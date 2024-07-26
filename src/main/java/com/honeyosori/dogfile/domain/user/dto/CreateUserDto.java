package com.honeyosori.dogfile.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.global.constant.Role;
import com.honeyosori.dogfile.global.constant.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

public record CreateUserDto(@NotNull String username,
                            @NotNull String name,
                            @NotNull String password,
                            @NotNull String profileImageUrl,
                            @NotNull Date birthday,
                            @NotNull String phoneNumber,
                            @NotNull @Email String email,
                            @NotNull Role role,
                            @NotNull UserStatus userStatus) {
    public User toUser() {
        return new User(username, name, password, profileImageUrl, birthday, phoneNumber, email, role, userStatus);
    }
}
