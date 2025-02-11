package com.honeyosori.dogfile.domain.feign.client;

import com.honeyosori.dogfile.domain.feign.dto.CreateDogclubUserDto;
import com.honeyosori.dogfile.domain.feign.dto.UpdateDogclubUserDto;
import com.honeyosori.dogfile.global.config.FeignOKHttpConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.honeyosori.dogfile.global.constant.DogUrl.*;

@FeignClient(name = "dogclub", configuration = FeignOKHttpConfig.class)
public interface DogclubClient {
    @GetMapping("/healthz")
    ResponseEntity<?> healthz();

    @PostMapping(DOGCLUB_REGISTER)
    ResponseEntity<?> register(@RequestBody CreateDogclubUserDto createDogclubUserDto);

    @PatchMapping(DOGCLUB_UPDATE + "/{dogfileUserId}")
    ResponseEntity<?> updateUser(@PathVariable String dogfileUserId, @RequestBody UpdateDogclubUserDto updateDogclubUserDto);

    @GetMapping(DOGCLUB_FIND + "/{dogfileUserId}")
    String findByDogfileUserId(@PathVariable String dogfileUserId);
}
