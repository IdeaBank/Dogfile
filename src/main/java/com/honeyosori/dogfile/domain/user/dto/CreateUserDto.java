package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.global.constant.Role;

public record CreateUserDto(String username, String password, Role role) {
    public User toUser() {
        return new User(username, password, role);
    }
}
