package com.honeyosori.dogfile.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.global.constant.Role;

public record CreateUserDto(@JsonInclude String username,
                            @JsonIgnore String password,
                            @JsonIgnore Role role) {
    public User toUser() {
        return new User(username, password, role);
    }
}
