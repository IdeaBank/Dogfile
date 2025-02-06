package com.honeyosori.dogfile.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateDogclubUserDto(
        @NotBlank String dogfileUserId,
        @NotBlank String accountName,
        @NotBlank String profileImageUrl) {
}
