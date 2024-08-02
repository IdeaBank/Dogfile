package com.honeyosori.dogfile.domain.dog.controller;

import com.honeyosori.dogfile.domain.dog.dto.CreateBreedDto;
import com.honeyosori.dogfile.domain.dog.dto.RegisterDogDto;
import com.honeyosori.dogfile.domain.dog.service.DogService;
import com.honeyosori.dogfile.global.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/dog")
public class DogController {
    private DogService dogService;

    @Autowired
    public void setDogService(DogService dogService) {
        this.dogService = dogService;
    }

    @PostMapping
    public ResponseEntity<?> registerDog(@Valid @RequestBody RegisterDogDto registerDogDto, Authentication authentication) {
        return BaseResponse.getResponseEntity(this.dogService.registerDog(registerDogDto, authentication.getName()));
    }

    @PostMapping("/breed")
    public ResponseEntity<?> createBreed(@Valid @RequestBody CreateBreedDto createBreedDto) {
        return BaseResponse.getResponseEntity(this.dogService.createBreed(createBreedDto));
    }

    @GetMapping("/breed")
    public ResponseEntity<?> getBreed() {
        return BaseResponse.getResponseEntity(this.dogService.getBreedList());
    }
}
