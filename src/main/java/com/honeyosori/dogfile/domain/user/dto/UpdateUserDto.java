package com.honeyosori.dogfile.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.honeyosori.dogfile.global.constant.Role;
import com.honeyosori.dogfile.global.constant.UserStatus;

public record UpdateUserDto(@JsonIgnore String password,
                            String profileImageUrl,
                            String phoneNumber,
                            String email,
                            Role role,
                            UserStatus userStatus) {
}

