package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusDto(@NotNull User.UserStatus userStatus) {
}
