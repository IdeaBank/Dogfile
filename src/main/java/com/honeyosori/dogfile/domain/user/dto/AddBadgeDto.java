package com.honeyosori.dogfile.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record AddBadgeDto(@NotNull Long badgeId) {
}
