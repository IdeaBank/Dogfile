package com.honeyosori.dogfile.domain.feign.dto;

import lombok.Builder;

@Builder
public record UpdateDogclubUserDto(
        String accountName,
        String profileImageUrl
) {
}
