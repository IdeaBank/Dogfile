package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.CreateUserDogDto;
import com.honeyosori.dogfile.domain.user.service.DogService;
import com.honeyosori.dogfile.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dogs")
public class DogController {
    private DogService dogService;

    @GetMapping("/all-breeds")
    public ResponseEntity<?> getAllDogBreeds() {
        return BaseResponse.getResponseEntity(this.dogService.getAllDogBreeds());
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUserDog(@RequestBody CreateUserDogDto createUserDogDto) {
        return BaseResponse.getResponseEntity(this.dogService.createUserDog(createUserDogDto));
    }
}
