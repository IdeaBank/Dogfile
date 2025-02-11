package com.honeyosori.dogfile.global.controller;

import com.honeyosori.dogfile.domain.feign.client.DogclubClient;
import com.honeyosori.dogfile.domain.feign.client.DogusClient;
import com.honeyosori.dogfile.global.response.dto.BaseResponse;
import com.honeyosori.dogfile.global.response.dto.GeneralResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GlobalController {

    private final DogusClient dogusClient;
    private final DogclubClient dogclubCient;
    @GetMapping("/healthz")
    public GeneralResponse checkServerOnline() {
        log.info(dogusClient.healthz().toString());
        log.info(dogclubCient.healthz().toString());
        return GeneralResponse.HEALTHZ;
    }
}
