package com.honeyosori.dogfile.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;

public record LoginDto(@NotNull String username,
                       @NotNull String password) {
}
