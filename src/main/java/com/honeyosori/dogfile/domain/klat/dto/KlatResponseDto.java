package com.honeyosori.dogfile.domain.klat.dto;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record KlatResponseDto(@NotNull Object user,
                              @NotNull String loginToken) {
}
