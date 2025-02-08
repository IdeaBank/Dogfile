package com.honeyosori.dogfile.domain.user.dto;

import lombok.Builder;

@Builder
public record UpdateDogclubUserDto(
        String accountName,
        String profileImageUrl
) {
}
