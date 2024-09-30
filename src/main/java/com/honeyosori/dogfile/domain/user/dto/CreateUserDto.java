package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

public record CreateUserDto(@NotNull String username,
                            @NotNull String realName,
                            @NotNull String password,
                            @NotNull String profileImageUrl,
                            @DateTimeFormat(pattern = "yyyy-MM-dd") @Past @NotNull Date birthday,
                            @NotNull String phoneNumber,
                            @NotNull String address,
                            @NotNull @Email String email,
                            @NotNull String preferCategory,
                            @NotNull User.GenderType gender) {
    public User toUser() {
        return new User(username, realName, password, profileImageUrl, birthday, phoneNumber, address, email, gender);
    }

    public CreateDogusUserDto toCreateDogusUserDto(String id) {
        return new CreateDogusUserDto(id, username, realName, password, profileImageUrl, birthday, phoneNumber, address, email, preferCategory, gender);
    }
}
