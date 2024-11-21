package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.CreateUserDogDto;
import com.honeyosori.dogfile.domain.user.dto.UpdateDogDto;
import com.honeyosori.dogfile.domain.user.service.DogService;
import com.honeyosori.dogfile.global.constant.CustomHeader;
import com.honeyosori.dogfile.global.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dogs")
public class DogController {
    private DogService dogService;

    @Autowired
    public void setDogService(DogService dogService) {
        this.dogService = dogService;
    }

    @GetMapping("/all-breeds")
    public ResponseEntity<?> getAllDogBreeds() {
        return BaseResponse.getResponseEntity(this.dogService.getAllDogBreeds());
    }

    @PostMapping
    public ResponseEntity<?> registerUserDog(@RequestBody CreateUserDogDto createUserDogDto) {
        return BaseResponse.getResponseEntity(this.dogService.createUserDog(createUserDogDto));
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
    @PatchMapping
    public ResponseEntity<?> updateDog(@RequestBody UpdateDogDto updateDogDto, @RequestParam("id") String id) {
        return BaseResponse.getResponseEntity(this.dogService.updateDog(updateDogDto, id));
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
    @GetMapping
    public ResponseEntity<?> findDogById(@RequestParam("id") String id) {
        return BaseResponse.getResponseEntity(this.dogService.findDogById(id));
    }

    @GetMapping("/by-dogfileUserId")
    public ResponseEntity<?> findDogByDogFileUserId(@RequestParam("dogfileUserId") String dogfileUserId) {
        return BaseResponse.getResponseEntity(this.dogService.findDogByDogFileUserId(dogfileUserId));
    }

    @GetMapping("/by-dogfileUserId-and-name")
    public ResponseEntity<?> findDogByDogFileUserIdAndName(@RequestParam("dogfileUserId") String dogfileUserId, @RequestParam("name") String name) {
        return BaseResponse.getResponseEntity(this.dogService.findDogByDogFileUserIdAndName(dogfileUserId, name));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteDog(@RequestHeader(CustomHeader.EMAIL) String email, @RequestParam String name) {
        return BaseResponse.getResponseEntity(this.dogService.deleteDog(email, name));
    }
    @GetMapping("/withdraw")
    public ResponseEntity<?> getWithdrawingDog() {
        return BaseResponse.getResponseEntity(this.dogService.getWithdrawingDog());
    }
}
