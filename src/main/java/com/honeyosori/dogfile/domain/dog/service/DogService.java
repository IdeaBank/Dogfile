package com.honeyosori.dogfile.domain.dog.service;

import com.honeyosori.dogfile.domain.dog.dto.CreateBreedDto;
import com.honeyosori.dogfile.domain.dog.dto.RegisterDogDto;
import com.honeyosori.dogfile.domain.dog.entity.Breed;
import com.honeyosori.dogfile.domain.dog.entity.Dog;
import com.honeyosori.dogfile.domain.dog.repository.BreedRepository;
import com.honeyosori.dogfile.domain.dog.repository.DogRepository;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class DogService {
    private final UserRepository userRepository;
    private final DogRepository dogRepository;
    private final BreedRepository breedRepository;

    @Autowired
    public DogService(UserRepository userRepository, DogRepository dogRepository, BreedRepository breedRepository) {
        this.userRepository = userRepository;
        this.dogRepository = dogRepository;
        this.breedRepository = breedRepository;
    }

    public BaseResponse<?> registerDog(RegisterDogDto registerDogDto) {
        // TODO: User ID
        Long userId = 1L;
        User owner = this.userRepository.getUserById(userId);
        Breed breed = this.breedRepository.findById(registerDogDto.breedId()).orElse(null);

        if(breed == null) {
            return new BaseResponse<>(BaseResponseStatus.BREED_NOT_FOUND, null);
        }

        Dog dog = registerDogDto.toDog();
        dog.setOwner(owner);
        dogRepository.save(dog);

        this.dogRepository.save(dog);

        return new BaseResponse<>(BaseResponseStatus.CREATED, registerDogDto);
    }

    public BaseResponse<?> createBreed(CreateBreedDto createBreedDto) {
        if (this.breedRepository.existsByName(createBreedDto.name())) {
            return new BaseResponse<>(BaseResponseStatus.BREED_EXIST, createBreedDto);
        }

        Breed breed = createBreedDto.toBreed();
        this.breedRepository.save(breed);

        return new BaseResponse<>(BaseResponseStatus.CREATED, createBreedDto);
    }

    public BaseResponse<?> getBreedList() {
        List<Breed> breedList = this.breedRepository.findAll();

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, breedList);
    }
}
