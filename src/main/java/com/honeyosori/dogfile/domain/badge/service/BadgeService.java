package com.honeyosori.dogfile.domain.badge.service;

import com.honeyosori.dogfile.domain.badge.dto.CreateBadgeDto;
import com.honeyosori.dogfile.domain.badge.entity.Badge;
import com.honeyosori.dogfile.domain.badge.repository.BadgeRepository;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;

    @Autowired
    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public BaseResponse<?> createBadge(CreateBadgeDto createBadgeDto) {
        if(this.badgeRepository.existsBadgeByTitle(createBadgeDto.title())) {
            return new BaseResponse<>(BaseResponseStatus.BADGE_EXIST, createBadgeDto);
        }

        Badge badge = createBadgeDto.toBadge();
        this.badgeRepository.save(badge);

        return new BaseResponse<>(BaseResponseStatus.CREATED, createBadgeDto);
    }
}
