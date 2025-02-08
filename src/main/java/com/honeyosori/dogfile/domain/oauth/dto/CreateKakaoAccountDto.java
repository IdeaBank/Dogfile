package com.honeyosori.dogfile.domain.oauth.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

public record CreateKakaoAccountDto(@NotNull String accountName,
                                    @NotNull String password,
                                    @NotNull String realName,
                                    @NotNull User.GenderType gender,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") @Past @NotNull Date birthday,
                                    @NotNull String phoneNumber,
                                    @NotNull String profileImageUrl,
                                    @NotNull String email) {
}
