package com.honeyosori.dogfile.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.global.constant.Role;
import com.honeyosori.dogfile.global.constant.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

public record CreateUserDto(@NotNull String username,
                            @NotNull String name,
                            @NotNull String password,
                            @NotNull String profileImageUrl,
                            @DateTimeFormat(pattern="yyyy-MM-dd") @Past @NotNull Date birthday,
                            @NotNull String phoneNumber,
                            @NotNull @Email String email) {
    public User toUser() {
        return new User(username, name, password, profileImageUrl, birthday, phoneNumber, email);
    }
}
