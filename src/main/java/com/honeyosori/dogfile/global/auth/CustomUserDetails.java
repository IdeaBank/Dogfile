package com.honeyosori.dogfile.global.auth;

import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.constant.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final CustomUserInfoDto customUserInfoDto;
    private final UserRepository userRepository;

    public CustomUserDetails(User user, UserRepository userRepository) {
        this.customUserInfoDto = new CustomUserInfoDto(user);
        this.userRepository = userRepository;
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
        return customUserInfoDto.getUsername();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return customUserInfoDto.getUserStatus() != UserStatus.WITHDRAW;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        User user = this.userRepository.getUserByUsername(customUserInfoDto.getUsername());

        return user.getPassword().equals(customUserInfoDto.getPassword());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
