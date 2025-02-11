package com.honeyosori.dogfile.domain.feign.dto;

import lombok.Builder;

@Builder
public record UpdateDogusUserDto(
        String accountName,
        String profileImageUrl
) {}
