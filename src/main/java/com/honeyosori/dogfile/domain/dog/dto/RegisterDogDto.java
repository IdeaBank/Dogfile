package com.honeyosori.dogfile.domain.dog.dto;

import com.honeyosori.dogfile.domain.dog.entity.Dog;
import jakarta.validation.constraints.NotNull;

public record RegisterDogDto(@NotNull String name,
                             @NotNull Long breedId) {
    public Dog toDog() {
        return new Dog(name);
    }
}
