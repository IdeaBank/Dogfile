package com.honeyosori.dogfile.global.config;

import com.honeyosori.dogfile.domain.badge.repository.BadgeRepository;
import com.honeyosori.dogfile.domain.badge.repository.OwnBadgeRepository;
import com.honeyosori.dogfile.domain.dog.repository.BreedRepository;
import com.honeyosori.dogfile.domain.dog.repository.DogRepository;
import com.honeyosori.dogfile.domain.dog.service.DogService;
import com.honeyosori.dogfile.domain.user.repository.BlockRepository;
import com.honeyosori.dogfile.domain.user.repository.FollowRepository;
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
    private final BlockRepository blockRepository;
    private final FollowRepository followRepository;
    private final DogRepository dogRepository;
    private final BreedRepository breedRepository;
    private final BadgeRepository badgeRepository;
    private final OwnBadgeRepository ownBadgeRepository;
    private final JwtUtility jwtUtility;

    @Bean
    public UserService userService() {
        return new UserService(userRepository, withdrawWaitingRepository, blockRepository, followRepository, badgeRepository, ownBadgeRepository, jwtUtility);
    }

    @Bean
    public DogService dogService() {
        return new DogService(userRepository, dogRepository, breedRepository);
    }
}