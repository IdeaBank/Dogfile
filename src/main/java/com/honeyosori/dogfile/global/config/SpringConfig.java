package com.honeyosori.dogfile.global.config;

import com.honeyosori.dogfile.domain.feign.client.DogclubClient;
import com.honeyosori.dogfile.domain.feign.client.DogusClient;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.domain.user.service.UserService;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SpringConfig {
    private final UserRepository userRepository;
    private final JwtUtility jwtUtility;
    private final RedisTemplate<String, String> redisTemplate;
    private final DogusClient dogusClient;
    private final DogclubClient dogclubClient;

    @Bean
    public UserService userService() {
        return new UserService(userRepository, jwtUtility, redisTemplate, dogusClient, dogclubClient);
    }
}