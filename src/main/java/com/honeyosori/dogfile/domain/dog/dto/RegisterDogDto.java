package com.honeyosori.dogfile.domain.dog.dto;

import com.honeyosori.dogfile.domain.dog.entity.Breed;
import com.honeyosori.dogfile.domain.dog.entity.Dog;

public record RegisterDogDto(String name, Breed breed) {
    public Dog toDog() {
        return new Dog(name, breed);
    }
}
