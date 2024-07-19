package com.honeyosori.dogfile.domain.user.service;

import com.honeyosori.dogfile.domain.badge.entity.Badge;
import com.honeyosori.dogfile.domain.badge.repository.BadgeRepository;
import com.honeyosori.dogfile.domain.badge.repository.OwnBadgeRepository;
import com.honeyosori.dogfile.domain.user.dto.CreateUserDto;
import com.honeyosori.dogfile.domain.user.dto.UpdateUserDto;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final OwnBadgeRepository ownBadgeRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, BadgeRepository badgeRepository, OwnBadgeRepository ownBadgeRepository) {
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
        this.ownBadgeRepository = ownBadgeRepository;
    }

    public BaseResponse<?> register(CreateUserDto createUserDto) {
        User user = createUserDto.toUser();
        user.setPassword(encoder.encode(user.getPassword()));

        this.userRepository.save(user);

        return new BaseResponse<>(BaseResponseStatus.CREATED, createUserDto);
    }

    public void updateUser(Long userId, UpdateUserDto updateUserDto) throws ClassNotFoundException {
        User user = this.userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ClassNotFoundException();
        }

        if (updateUserDto.password() != null) {
            user.setPassword(encoder.encode(updateUserDto.password()));
        }

        if (updateUserDto.role() != null) {
            user.setRole(updateUserDto.role());
        }

        this.userRepository.save(user);
    }

    public void deleteUser(Long userId) throws ClassNotFoundException {
        User user = this.userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ClassNotFoundException();
        }

        this.userRepository.delete(user);
    }

    public void addBadge(Long userId, Long badgeId) throws ClassNotFoundException {
        User user = this.userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ClassNotFoundException();
        }

        Badge badge = this.badgeRepository.findById(badgeId).orElse(null);

        if (badge == null) {
            throw new ClassNotFoundException();
        }
    }
}
