package com.honeyosori.dogfile.global.auth;

import com.honeyosori.dogfile.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CustomUserInfoDto {
    private String email;
    private String password;
    private User.Role role;
    private User.UserStatus userStatus;

    public CustomUserInfoDto(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.userStatus = user.getUserStatus();
    }
}
