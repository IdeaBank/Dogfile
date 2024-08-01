package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.global.constant.Role;
import com.honeyosori.dogfile.global.constant.UserStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

public record UpdateUserDto(@NotNull String password,
                            @NotNull String profileImageUrl,
                            @DateTimeFormat(pattern="yyyy-MM-dd") @Past @NotNull Date birthday,
                            @NotNull String phoneNumber,
                            @NotNull String email,
                            @NotNull Role role,
                            @NotNull UserStatus userStatus) {
}