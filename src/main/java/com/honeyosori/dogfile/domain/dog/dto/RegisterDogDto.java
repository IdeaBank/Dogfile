package com.honeyosori.dogfile.domain.dog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.honeyosori.dogfile.domain.dog.entity.Breed;
import com.honeyosori.dogfile.domain.dog.entity.Dog;

public record RegisterDogDto(@JsonInclude String name,
                             @JsonInclude Long breedId) {
    public Dog toDog() {
        return new Dog(name);
    }
}
