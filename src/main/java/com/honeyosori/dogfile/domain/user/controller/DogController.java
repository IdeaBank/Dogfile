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

    @PatchMapping
    public ResponseEntity<?> updateDog(@RequestBody UpdateDogDto updateDogDto, @RequestParam("id") String id) {
        return BaseResponse.getResponseEntity(this.dogService.updateDog(updateDogDto, id));
    }

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
