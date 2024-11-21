package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.Dog;
import com.honeyosori.dogfile.domain.user.entity.User;

public record UpdateDogDto(
        User dogfileUser,
        String name,
        Dog.Size size,
        String dogImage) {
}