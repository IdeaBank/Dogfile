package com.honeyosori.dogfile.domain.badge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.honeyosori.dogfile.domain.badge.entity.Badge;

public record CreateBadgeDto(@JsonInclude String title,
                             @JsonInclude String description) {
    public Badge toBadge() {
        return new Badge(title, description);
    }
}