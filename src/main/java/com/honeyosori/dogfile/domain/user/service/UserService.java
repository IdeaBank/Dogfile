package com.honeyosori.dogfile.domain.user.service;

import com.honeyosori.dogfile.domain.badge.repository.BadgeRepository;
import com.honeyosori.dogfile.domain.user.dto.CreateUserDto;
import com.honeyosori.dogfile.domain.user.dto.UpdateUserDto;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, BadgeRepository badgeRepository) {
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
    }

    public void register(CreateUserDto createUserDto) {
        User user = createUserDto.toUser();
        user.setPassword(encoder.encode(user.getPassword()));

        this.userRepository.save(user);
    }

    public void updateUser(Long userId, UpdateUserDto updateUserDto) throws ClassNotFoundException {
        User user = this.userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ClassNotFoundException();
        }

        if (updateUserDto.getPassword() != null) {
            user.setPassword(encoder.encode(updateUserDto.getPassword()));
        }

        if (updateUserDto.getRole() != null) {
            user.setRole(updateUserDto.getRole());
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
}
