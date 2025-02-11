package com.honeyosori.dogfile.domain.feign.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateDogusUserDto(
        @NotBlank String dogfileUserId,
        @NotBlank String accountName,
        @NotBlank String profileImageUrl) {
}
