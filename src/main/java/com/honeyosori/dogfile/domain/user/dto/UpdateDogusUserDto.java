package com.honeyosori.dogfile.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public record UpdateDogusUserDto(
        String accountName,
        String profileImageUrl
) {}
