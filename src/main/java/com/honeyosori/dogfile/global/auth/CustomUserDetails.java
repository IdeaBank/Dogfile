package com.honeyosori.dogfile.global.auth;

import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.entity.User.UserStatus;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final CustomUserInfoDto customUserInfoDto;

    public CustomUserDetails(User user, UserRepository userRepository) {
        this.customUserInfoDto = new CustomUserInfoDto(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> authorities = new ArrayList<>();
        authorities.add("ROLE_" + customUserInfoDto.getRole());

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return customUserInfoDto.getPassword();
    }

    @Override
    public String getUsername() {
        return customUserInfoDto.getEmail();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return customUserInfoDto.getUserStatus() != UserStatus.WITHDRAWN;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
