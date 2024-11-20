package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDateTime;

public record UserInfoDto(String id,
                          String accountName,
                          String realName,
                          User.GenderType gender,
                          @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date birthday,
                          String phoneNumber,
                          String profileImageUrl,
                          String email,
                          User.Role role,
                          LocalDateTime createdAt,
                          Boolean deleted,
                          LocalDateTime withdrawRequestAt) {
    public static UserInfoDto of(User user) {
        return new UserInfoDto(
                user.getId(),
                user.getAccountName(),
                user.getRealName(),
                user.getGender(),
                user.getBirthday(),
                user.getPhoneNumber(),
                user.getProfileImageUrl(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getDeleted(),
                user.getWithdrawRequestAt()
        );
    }
}
