package com.honeyosori.dogfile.domain.user.service;

import com.honeyosori.dogfile.domain.badge.entity.*;
import com.honeyosori.dogfile.domain.badge.repository.*;
import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.entity.*;
import com.honeyosori.dogfile.domain.user.identity.*;
import com.honeyosori.dogfile.domain.user.repository.*;
import com.honeyosori.dogfile.global.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final FollowRepository followRepository;
    private final BadgeRepository badgeRepository;
    private final OwnBadgeRepository ownBadgeRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, BlockRepository blockRepository, FollowRepository followRepository, BadgeRepository badgeRepository, OwnBadgeRepository ownBadgeRepository) {
        this.userRepository = userRepository;
        this.blockRepository = blockRepository;
        this.followRepository = followRepository;
        this.badgeRepository = badgeRepository;
        this.ownBadgeRepository = ownBadgeRepository;
    }

    public BaseResponse<?> register(CreateUserDto createUserDto) {
        User user = createUserDto.toUser();
        user.setPassword(encoder.encode(user.getPassword()));

        this.userRepository.save(user);

        return new BaseResponse<>(BaseResponseStatus.CREATED, createUserDto);
    }

    public BaseResponse<?> updateUser(UpdateUserDto updateUserDto) {
        // TODO: User ID
        Long userId = 1L;
        User user = this.userRepository.getUserById(userId);

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

    public BaseResponse<?> deleteUser() {
        // TODO: User ID
        Long userId = 1L;
        User user = this.userRepository.getUserById(userId);
        this.userRepository.delete(user);

        return new BaseResponse<>(BaseResponseStatus.DELETED, null);
    }

    public BaseResponse<?> addBadge(Long badgeId) {
        // TODO: User ID
        Long userId = 1L;
        User user = this.userRepository.getUserById(userId);
        Badge badge = this.badgeRepository.findById(badgeId).orElse(null);

        if (badge == null) {
            return new BaseResponse<>(BaseResponseStatus.BADGE_NOT_FOUND, null);
        }

        boolean isBadgeAdded = this.ownBadgeRepository.existsByUserIdAndBadgeId(userId, badgeId);

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

    public BaseResponse<?> follow(FollowDto followDto) {
        // TODO: User ID
        Long userId = 1L;
        User user = this.userRepository.getUserById(userId);
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

    public BaseResponse<?> unfollow(FollowDto followDto) {
        // TODO: User ID
        Long userId = 1L;
        Long followeeId = followDto.followeeId();

        Follow follow = this.followRepository.findByFollowIdentityFollowerIdAndFollowIdentityFolloweeId(userId, followeeId);

        if (follow == null) {
            return new BaseResponse<>(BaseResponseStatus.NOT_FOLLOWING, null);
        }

        this.followRepository.delete(follow);

        return new BaseResponse<>(BaseResponseStatus.DELETED, null);
    }

    public BaseResponse<?> block(BlockDto blockDto) {
        // TODO: User ID
        Long userId = 1L;
        User user = this.userRepository.getUserById(userId);
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

    public BaseResponse<?> unblock(BlockDto blockDto) {
        // TODO: User ID
        Long userId = 1L;
        Long blockeeId = blockDto.blockeeId();

        Block block = this.blockRepository.findBlockByBlockIdentityBlockerIdAndBlockIdentityBlockeeId(userId, blockeeId);

        if (block == null) {
            return new BaseResponse<>(BaseResponseStatus.NOT_BLOCKING, null);
        }

        this.blockRepository.delete(block);

        return new BaseResponse<>(BaseResponseStatus.DELETED, null);
    }
}
