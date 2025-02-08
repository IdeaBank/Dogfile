package com.honeyosori.dogfile.domain.user.dto;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public record UpdateDogclubUserDto(
        String accountName,
        String profileImageUrl
) {
}
