package com.honeyosori.dogfile.domain.dog.service;

import com.honeyosori.dogfile.domain.dog.dto.RegisterDogDto;
import com.honeyosori.dogfile.domain.dog.entity.Dog;
import com.honeyosori.dogfile.domain.dog.repository.BreedRepository;
import com.honeyosori.dogfile.domain.dog.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DogService {
    private final DogRepository dogRepository;
    private final BreedRepository breedRepository;

    @Autowired
    public DogService(DogRepository dogRepository, BreedRepository breedRepository) {
        this.dogRepository = dogRepository;
        this.breedRepository = breedRepository;
    }

    public void registerDog(RegisterDogDto registerDogDto) {
        Dog dog = registerDogDto.toDog();

        dogRepository.save(dog);
    }
}
