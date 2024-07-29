package com.honeyosori.dogfile.domain.dog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.honeyosori.dogfile.domain.dog.entity.Breed;
import jakarta.validation.constraints.NotNull;

public record CreateBreedDto(@NotNull String name) {
    public Breed toBreed() {
        return new Breed(name);
    }
}
