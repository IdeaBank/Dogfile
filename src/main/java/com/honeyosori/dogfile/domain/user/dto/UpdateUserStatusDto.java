package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.global.constant.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusDto(@NotNull UserStatus userStatus) {
}
