package com.honeyosori.dogfile.domain.dog.controller;

import com.honeyosori.dogfile.domain.dog.dto.CreateBreedDto;
import com.honeyosori.dogfile.domain.dog.dto.RegisterDogDto;
import com.honeyosori.dogfile.domain.dog.service.DogService;
import com.honeyosori.dogfile.global.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public BaseResponse<?> registerDog(@RequestBody RegisterDogDto registerDogDto) {
        return this.dogService.registerDog(registerDogDto);
    }

    @PostMapping
    public BaseResponse<?> createBreed(@RequestBody CreateBreedDto createBreedDto) {
        return this.dogService.createBreed(createBreedDto);
    }
}
