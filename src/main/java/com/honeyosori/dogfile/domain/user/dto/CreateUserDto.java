package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

public record CreateUserDto(@NotNull @Email String email,
                            @NotNull String password,
                            @NotNull String realName,
                            @NotNull User.GenderType gender,
                            @DateTimeFormat(pattern = "yyyy-MM-dd") @Past @NotNull Date birthday,
                            @NotNull String phoneNumber,
                            @NotNull String address,
                            @NotNull String profileImageUrl,
                            @NotNull String preferCategory) {
    public User toUser() {
        return new User(email, password, realName, gender, birthday, phoneNumber, address, profileImageUrl);
    }

    public CreateDogusUserDto toCreateDogusUserDto(String id) {
        return new CreateDogusUserDto(id, email, password, realName, gender, birthday, phoneNumber, address, profileImageUrl);
    }
}
