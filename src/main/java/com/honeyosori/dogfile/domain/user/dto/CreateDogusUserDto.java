package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

public record CreateDogusUserDto(@NotNull String id,
                                 @NotNull String username,
                                 @NotNull String realName,
                                 @NotNull String password,
                                 @NotNull String profileImageUrl,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @Past @NotNull Date birthday,
                                 @NotNull String phoneNumber,
                                 @NotNull String address,
                                 @NotNull @Email String email,
                                 @NotNull String preferCategory,
                                 @NotNull User.GenderType gender) {
}
