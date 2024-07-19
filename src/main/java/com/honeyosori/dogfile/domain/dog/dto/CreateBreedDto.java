package com.honeyosori.dogfile.domain.dog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.honeyosori.dogfile.domain.dog.entity.Breed;

public record CreateBreedDto(@JsonInclude String name) {
    public Breed toBreed() {
        return new Breed(name);
    }
}
