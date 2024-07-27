package com.honeyosori.dogfile.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.honeyosori.dogfile.global.constant.Role;
import com.honeyosori.dogfile.global.constant.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserDto(@NotNull String password,
                            @NotNull String profileImageUrl,
                            @NotNull String phoneNumber,
                            @NotNull String email,
                            @NotNull Role role,
                            @NotNull UserStatus userStatus) {
}