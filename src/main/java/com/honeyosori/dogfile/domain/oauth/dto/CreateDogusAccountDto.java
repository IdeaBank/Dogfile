package com.honeyosori.dogfile.domain.oauth.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

public record CreateDogusAccountDto(String email,
                                    String password,
                                    String realName,
                                    User.GenderType gender,
                                    Date birthday,
                                    String phoneNumber,
                                    String address,
                                    String profileImageUrl,
                                    String preferCategory,
                                    String username) {
}
