package com.honeyosori.dogfile.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateKakaoAccountDto(@NotNull String kakaoAccessToken,
                                    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date birthday,
                                    @NotNull String phoneNumber,
                                    @NotNull User.GenderType gender,
                                    @NotNull String realName,
                                    @NotNull String accountName) {
}
