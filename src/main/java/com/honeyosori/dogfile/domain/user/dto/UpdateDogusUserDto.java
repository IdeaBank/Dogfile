package com.honeyosori.dogfile.domain.user.dto;

import lombok.Builder;

@Builder
public record UpdateDogusUserDto(
        String accountName,
        String profileImageUrl
) {}
