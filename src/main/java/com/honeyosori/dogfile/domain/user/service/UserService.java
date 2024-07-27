package com.honeyosori.dogfile.domain.user.service;

import com.honeyosori.dogfile.domain.badge.entity.*;
import com.honeyosori.dogfile.domain.badge.repository.*;
import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.entity.*;
import com.honeyosori.dogfile.domain.user.identity.*;
import com.honeyosori.dogfile.domain.user.repository.*;
import com.honeyosori.dogfile.global.response.*;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final FollowRepository followRepository;
    private final BadgeRepository badgeRepository;
    private final OwnBadgeRepository ownBadgeRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtility jwtUtility;

    @Autowired
    public UserService(UserRepository userRepository, BlockRepository blockRepository, FollowRepository followRepository, BadgeRepository badgeRepository, OwnBadgeRepository ownBadgeRepository, JwtUtility jwtUtility) {
        this.userRepository = userRepository;
        this.blockRepository = blockRepository;
        this.followRepository = followRepository;
        this.badgeRepository = badgeRepository;
        this.ownBadgeRepository = ownBadgeRepository;
        this.jwtUtility = jwtUtility;
    }

    @Transactional
    public BaseResponse<?> register(CreateUserDto createUserDto) {
        String username = createUserDto.username();

        User user = this.userRepository.findUserByUsername(username).orElse(null);

        if(user != null) {
            return new BaseResponse<>(BaseResponseStatus.USERNAME_EXISTS, null);
        }

        User newUser = createUserDto.toUser();
        newUser.setPassword(encoder.encode(newUser.getPassword()));

        this.userRepository.save(newUser);

        return new BaseResponse<>(BaseResponseStatus.CREATED, createUserDto);
    }

    public BaseResponse<?> updateUser(UpdateUserDto updateUserDto, String username) {
        User user = this.userRepository.getUserByUsername(username);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.INVALID_JWT_TOKEN, null);
        }

        if (updateUserDto.password() != null) {
            user.setPassword(encoder.encode(updateUserDto.password()));
        }

        if (updateUserDto.profileImageUrl() != null) {
            user.setProfileImageUrl(updateUserDto.profileImageUrl());
        }

        if (updateUserDto.phoneNumber() != null) {
            user.setPhoneNumber(updateUserDto.phoneNumber());
        }

        if (updateUserDto.email() != null) {
            user.setEmail(updateUserDto.email());
        }

        if (updateUserDto.role() != null) {
            user.setRole(updateUserDto.role());
        }

        if (updateUserDto.userStatus() != null) {
            user.setUserStatus(updateUserDto.userStatus());
        }

        this.userRepository.save(user);

        return new BaseResponse<>(BaseResponseStatus.UPDATED, updateUserDto);
    }

    public ResponseEntity<?> login(LoginDto loginDto) {
        String username = loginDto.username();
        String password = loginDto.password();

        User user = this.userRepository.findUserByUsername(username).orElse(null);

        if (user == null) {
            return BaseResponse.getResponseEntity(new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null));
        }

        if(encoder.matches(password, user.getPassword())) {
            String accessToken = jwtUtility.generateAccessToken(username);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Authorization", accessToken);

            HttpCookie cookie = new HttpCookie("refresh_token", accessToken);
            cookie.setPath("/");

            responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString().replace("\"", ""));

            return ResponseEntity.ok().headers(responseHeaders).build();
        }

        return BaseResponse.getResponseEntity(new BaseResponse<>(BaseResponseStatus.WRONG_PASSWORD, null));
    }

    public BaseResponse<?> deleteUser(String username) {
        User user = this.userRepository.getUserByUsername(username);
        this.userRepository.delete(user);

        return new BaseResponse<>(BaseResponseStatus.DELETED, null);
    }

    public BaseResponse<?> addBadge(AddBadgeDto addBadgeDto, String username) {
        User user = this.userRepository.getUserByUsername(username);
        Long badgeId = addBadgeDto.badgeId();
        Badge badge = this.badgeRepository.findById(badgeId).orElse(null);

        if (badge == null) {
            return new BaseResponse<>(BaseResponseStatus.BADGE_NOT_FOUND, null);
        }

        boolean isBadgeAdded = this.ownBadgeRepository.existsByUserIdAndBadgeId(user.getId(), badgeId);

        if (isBadgeAdded) {
            return new BaseResponse<>(BaseResponseStatus.ALREADY_OWN_BADGE, null);
        }

        OwnBadge ownBadge = new OwnBadge(user, badge);
        this.ownBadgeRepository.save(ownBadge);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, ownBadge);
    }

    public BaseResponse<?> getUserInfo(Long userId) {
        User user = this.userRepository.getUserById(userId);
        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    public BaseResponse<?> follow(FollowDto followDto, String username) {
        User user = this.userRepository.getUserByUsername(username);
        Long followeeId = followDto.followeeId();
        User followee = this.userRepository.findById(followeeId).orElse(null);

        if (followee == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        FollowIdentity followIdentity = new FollowIdentity(user, followee);
        Follow follow = new Follow(followIdentity);

        this.followRepository.save(follow);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, follow);
    }

    public BaseResponse<?> unfollow(FollowDto followDto, String username) {
        User user = this.userRepository.getUserByUsername(username);
        Long followeeId = followDto.followeeId();

        Follow follow = this.followRepository.findByFollowIdentityFollowerIdAndFollowIdentityFolloweeId(user.getId(), followeeId);

        if (follow == null) {
            return new BaseResponse<>(BaseResponseStatus.NOT_FOLLOWING, null);
        }

        this.followRepository.delete(follow);

        return new BaseResponse<>(BaseResponseStatus.DELETED, null);
    }

    public BaseResponse<?> block(BlockDto blockDto, String username) {
        User user = this.userRepository.getUserByUsername(username);
        Long blockeeId = blockDto.blockeeId();
        User blockee = this.userRepository.findById(blockeeId).orElse(null);

        if (blockee == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        BlockIdentity blockIdentity = new BlockIdentity(user, blockee);
        Block block = new Block(blockIdentity);

        this.blockRepository.save(block);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, block);
    }

    public BaseResponse<?> unblock(BlockDto blockDto, String username) {
        User user = this.userRepository.getUserByUsername(username);
        Long blockeeId = blockDto.blockeeId();

        Block block = this.blockRepository.findBlockByBlockIdentityBlockerIdAndBlockIdentityBlockeeId(user.getId(), blockeeId);

        if (block == null) {
            return new BaseResponse<>(BaseResponseStatus.NOT_BLOCKING, null);
        }

        this.blockRepository.delete(block);

        return new BaseResponse<>(BaseResponseStatus.DELETED, null);
    }
}
