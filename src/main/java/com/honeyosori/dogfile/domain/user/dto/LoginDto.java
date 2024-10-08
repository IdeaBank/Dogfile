package com.honeyosori.dogfile.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record LoginDto(@NotNull String email,
                       @NotNull String password) {
}
