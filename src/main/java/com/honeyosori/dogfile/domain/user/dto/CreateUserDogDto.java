package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.Dog;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record CreateUserDogDto (
        @NotNull String email,
        @NotNull String name,
        @NotNull Long dogBreedId,
        @DateTimeFormat(pattern = "yyyy-MM-dd") @Past @NotNull Date birthday,
        @NotNull Dog.Size size,
        String dogImage
        ) {
}
