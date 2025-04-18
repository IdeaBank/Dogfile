package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record CreateUserDto(@NotNull String accountName,
                            @NotNull String password,
                            @NotNull String realName,
                            @NotNull User.GenderType gender,
                            @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date birthday,
                            @NotNull String phoneNumber,
                            @NotNull @Email String email) {
    public User toUser() {
        return new User(email, password, birthday, phoneNumber, gender, realName, accountName);
    }
}
