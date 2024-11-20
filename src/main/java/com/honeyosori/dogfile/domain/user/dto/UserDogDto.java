package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.Dog;
import com.honeyosori.dogfile.domain.user.entity.DogBreed;
import com.honeyosori.dogfile.domain.user.entity.User;

public record UserDogDto(
        User dogfileUser,
        String name,
        DogBreed breed,
        String dogImage) {

    public static UserDogDto of(Dog dog) {
        return new UserDogDto(dog.getDogfileUser(), dog.getName(), dog.getBreed(), dog.getDogImage());
    }
}
