package com.honeyosori.dogfile.domain.user.service;

import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.entity.Dog;
import com.honeyosori.dogfile.domain.user.entity.DogBreed;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.DogBreedRepository;
import com.honeyosori.dogfile.domain.user.repository.DogRepository;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DogService {
    private final UserRepository userRepository;
    private final DogRepository dogRepository;
    private final DogBreedRepository dogBreedRepository;

    @Autowired
    public DogService(UserRepository userRepository, DogRepository dogRepository, DogBreedRepository dogBreedRepository) {
        this.userRepository = userRepository;
        this.dogRepository = dogRepository;
        this.dogBreedRepository = dogBreedRepository;
    }

    public BaseResponse<?> getUserDogs(String dogfileUserEmail) {
        List<Dog> dogs = this.dogRepository.findAllByDogfileUserEmail(dogfileUserEmail);
        if (dogs == null) {
            return new BaseResponse<>(BaseResponseStatus.DOG_NOT_FOUND, null);
        }
        return (BaseResponse<?>) dogs.stream()
                .map(UserDogDto::of)
                .toList();
    }

    public BaseResponse<?> getAllDogBreeds() {
        List<DogBreed> dogBreeds = this.dogBreedRepository.findAll();
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

//        dogRepository.save(dog);

        return new BaseResponse<>(BaseResponseStatus.CREATED, dog);
    }

    /*
    Todo : 회원 반려견 정보 변경하기
    이름, 생일 등 정보 변경

    given)
    DB에 저장된 dog
    변경가능한 것)
    name, breed, size, birthday, dog_image
    이 중 부분적으로 입력을 받음

    when)
    정보 수정

    then)
    입력받은 새로운 데이터가 저장됨
     */
    @Transactional
    public BaseResponse<?> updateDog(UpdateDogDto updateDogDto, String id) {
        Dog dog = this.dogRepository.findById(id).orElse(null);

        if (dog == null || dog.getDeleted()) {
            return new BaseResponse<>(BaseResponseStatus.DOG_NOT_FOUND, null);
        }

        if (updateDogDto.dogfileUser() != null) {
            dog.setDogfileUser(updateDogDto.dogfileUser());
        }

        if (updateDogDto.name() != null) {
            dog.setName(updateDogDto.name());
        }

        if (updateDogDto.size() != null) {
            dog.setSize(updateDogDto.size());
        }

        if (updateDogDto.dogImage() != null) {
            dog.setDogImage(updateDogDto.dogImage());
        }

        return new BaseResponse<>(BaseResponseStatus.UPDATED, updateDogDto);
    }

    /*
    Todo : 회원 반려견 정보 검색하기
    검색 키에 따라 index 생성 및 회원 검색

    given)
    DB에 저장된 dog

    when)
    1. id로 검색
    2. dogfile user id로 검색
    3. dogfile user id와 name으로 검색

    then)
    있으면 dog 반환
    없으면 error handling
     */

    public BaseResponse<?> findDogById(String id) {
        Dog dog = this.dogRepository.findById(id).orElse(null);

        if (dog == null || dog.getDeleted()) {
            return new BaseResponse<>(BaseResponseStatus.DOG_NOT_FOUND, null);
        }

        DogInfoDto dogInfoDto = DogInfoDto.of(dog);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, dogInfoDto);
    }

    public BaseResponse<?> findDogByDogFileUserId(String dogFileUserId) {
        List<Dog> dogs = this.dogRepository.findAllByDogfileUserId(dogFileUserId);

        if (dogs.isEmpty() || dogs.stream().allMatch(Dog::getDeleted)) {
            return new BaseResponse<>(BaseResponseStatus.DOG_NOT_FOUND, null);
        }

        List<DogInfoDto> dogInfoDtos = dogs.stream()
                .map(DogInfoDto::of)
                .toList();

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, dogInfoDtos);
    }

    public BaseResponse<?> findDogByDogFileUserIdAndName(String dogfileUserId, String name) {
        Dog dog = this.dogRepository.findByDogfileUserIdAndName(dogfileUserId, name);

        if (dog == null || dog.getDeleted()) {
            return new BaseResponse<>(BaseResponseStatus.DOG_NOT_FOUND, null);
        }

        DogInfoDto dogInfoDto = DogInfoDto.of(dog);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, dogInfoDto);
    }

    @Transactional
    public BaseResponse<?> deleteDog(String email, String name) {
        Dog dog = this.dogRepository.findByNameAndDogfileUserEmail(name, email);

        dog.setDeleted(true);
        dog.setWithdrawRequestAt(LocalDateTime.now());
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
    }

    public BaseResponse<?> getWithdrawingDog() {
        List<Dog> dogs = this.dogRepository.findByDeleted();

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, dogs);
    }
}
