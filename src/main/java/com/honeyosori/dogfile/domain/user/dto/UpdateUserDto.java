package com.honeyosori.dogfile.domain.user.dto;

import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

public record UpdateUserDto(String password,
                            String profileImageUrl,
                            @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date birthday,
                            String phoneNumber) {
}