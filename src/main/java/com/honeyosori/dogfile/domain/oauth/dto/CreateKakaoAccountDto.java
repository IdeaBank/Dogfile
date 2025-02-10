package com.honeyosori.dogfile.domain.oauth.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record CreateKakaoAccountDto(@NotNull String kakaoAccessToken,
                                    @DateTimeFormat(pattern = "yyyy.MM.dd") @Past @NotNull Date birthday,
                                    @NotNull String phoneNumber,
                                    @NotNull User.GenderType gender,
                                    @NotNull String realName,
                                    @NotNull String accountName) {
}
