package com.honeyosori.dogfile.domain.badge.controller;

import com.honeyosori.dogfile.domain.badge.dto.CreateBadgeDto;
import com.honeyosori.dogfile.domain.badge.service.BadgeService;
import com.honeyosori.dogfile.global.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/badge")
public class BadgeController {
    private BadgeService badgeService;

    @Autowired
    public void setBadgeService(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @PostMapping
    public HttpEntity<?> createBadge(@Valid @RequestBody CreateBadgeDto createBadgeDto) {
        return BaseResponse.getResponseEntity(this.badgeService.createBadge(createBadgeDto));
    }

    @GetMapping
    public ResponseEntity<?> getAllBadges() {
        return BaseResponse.getResponseEntity(this.badgeService.getAllBadge());
    }
}