package com.honeyosori.dogfile.domain.badge.controller;

import com.honeyosori.dogfile.domain.badge.dto.CreateBadgeDto;
import com.honeyosori.dogfile.domain.badge.service.BadgeService;
import com.honeyosori.dogfile.global.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    public BaseResponse<?> createBadge(@RequestBody CreateBadgeDto createBadgeDto) {
        return this.badgeService.createBadge(createBadgeDto);
    }
}