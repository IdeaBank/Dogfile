package com.honeyosori.dogfile.global.config;

import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.domain.user.repository.WithdrawWaitingRepository;
import com.honeyosori.dogfile.domain.user.service.UserService;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SpringConfig {
    private final UserRepository userRepository;
    private final WithdrawWaitingRepository withdrawWaitingRepository;
    private final JwtUtility jwtUtility;

    @Bean
    public UserService userService() {
        return new UserService(userRepository, withdrawWaitingRepository, jwtUtility);
    }
}