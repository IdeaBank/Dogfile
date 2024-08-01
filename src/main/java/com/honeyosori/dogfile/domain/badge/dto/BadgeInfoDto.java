package com.honeyosori.dogfile.domain.badge.dto;

import com.honeyosori.dogfile.domain.badge.entity.Badge;

public record BadgeInfoDto(Long id, String title, String description) {
    public static BadgeInfoDto of(Badge badge) {
        return new BadgeInfoDto(badge.getId(), badge.getTitle(), badge.getDescription());
    }
}
