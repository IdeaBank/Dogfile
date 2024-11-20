package com.honeyosori.dogfile.domain.user.service;

import com.honeyosori.dogfile.domain.user.dto.CreateUserDogDto;
import com.honeyosori.dogfile.domain.user.dto.UserDogDto;
import com.honeyosori.dogfile.domain.user.entity.Dog;
import com.honeyosori.dogfile.domain.user.entity.DogBreed;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.DogBreedRepository;
import com.honeyosori.dogfile.domain.user.repository.DogRepository;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;

import java.util.List;
import java.util.Optional;

public class DogService {
    private UserRepository userRepository;
    private DogRepository dogRepository;
    private DogBreedRepository dogBreedRepository;

    public BaseResponse<?> getUserDogs(String dogfileUserEmail) {
        List<Dog> dogs = dogRepository.findAllByDogfileUserEmail(dogfileUserEmail);
        if (dogs == null) {
            return new BaseResponse<>(BaseResponseStatus.DOG_NOT_FOUND, null);
        }
        return (BaseResponse<?>) dogs.stream()
                .map(UserDogDto::of)
                .toList();
    }
    public BaseResponse<?> getAllDogBreeds() {
        List<DogBreed> dogBreeds = dogBreedRepository.findAll();
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, dogBreeds);
    }
    public BaseResponse<?> createUserDog(CreateUserDogDto createUserDogDto) {
        User dogfileUser = this.userRepository.findUserByEmail(createUserDogDto.email()).orElse(null);
        DogBreed dogBreed = this.dogBreedRepository.findById(createUserDogDto.dogBreedId());
        Dog dog = new Dog(
                dogfileUser,
                createUserDogDto.name(),
                dogBreed,
                createUserDogDto.birthday(),
                createUserDogDto.size(),
                createUserDogDto.dogImage()
        );
        return new BaseResponse<>(BaseResponseStatus.CREATED, dog);
    }
}
