package com.honeyosori.dogfile.domain.badge.dto;

import com.honeyosori.dogfile.domain.badge.entity.Badge;

public record CreateBadgeDto(String title, String description) {
    public Badge toBadge() {
        return new Badge(title, description);
    }
}