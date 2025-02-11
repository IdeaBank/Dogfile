package com.honeyosori.dogfile.domain.feign.client;

import com.honeyosori.dogfile.domain.feign.dto.CreateDogusUserDto;
import com.honeyosori.dogfile.domain.feign.dto.UpdateDogusUserDto;
import com.honeyosori.dogfile.global.config.FeignOKHttpConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.honeyosori.dogfile.global.constant.DogUrl.*;

@FeignClient(name = "dogus", configuration = FeignOKHttpConfig.class)
public interface DogusClient {
    @GetMapping("/healthz")
    ResponseEntity<?> healthz();

    @PostMapping(DOGUS_REGISTER)
    ResponseEntity<?> register(@RequestBody CreateDogusUserDto createDogusUserDto);

    @DeleteMapping(DOGUS_DELETE + "/{dogfileUserId}")
    ResponseEntity<?> deleteUser(@PathVariable String dogfileUserId);

    @PatchMapping(DOGUS_UPDATE + "/{dogfileUserId}")
    ResponseEntity<?> updateUser(@PathVariable String dogfileUserId, @RequestBody UpdateDogusUserDto updateDogusUserDto);
    @GetMapping(DOGUS_FIND + "/{dogfileUserId}")
    String findByDogfileUserId(@PathVariable String dogfileUserId);
}
