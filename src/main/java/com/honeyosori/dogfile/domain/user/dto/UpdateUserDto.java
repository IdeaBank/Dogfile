package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record UpdateUserDto(
        String accountName,
        String password,
        String realName,
        User.GenderType gender,
        @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date birthday,
        String phoneNumber,
        String profileImageUrl,
        String email) {
}