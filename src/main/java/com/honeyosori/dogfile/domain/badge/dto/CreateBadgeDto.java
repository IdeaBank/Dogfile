package com.honeyosori.dogfile.domain.badge.dto;

import com.honeyosori.dogfile.domain.badge.entity.Badge;
import jakarta.validation.constraints.NotNull;

public record CreateBadgeDto(@NotNull String title,
                             @NotNull String description) {
    public Badge toBadge() {
        return new Badge(title, description);
    }
}