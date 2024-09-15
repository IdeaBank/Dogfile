package com.honeyosori.dogfile.domain.user.service;

import com.honeyosori.dogfile.domain.badge.entity.Badge;
import com.honeyosori.dogfile.domain.badge.entity.OwnBadge;
import com.honeyosori.dogfile.domain.badge.repository.BadgeRepository;
import com.honeyosori.dogfile.domain.badge.repository.OwnBadgeRepository;
import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.entity.Block;
import com.honeyosori.dogfile.domain.user.entity.Follow;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.entity.User.*;
import com.honeyosori.dogfile.domain.user.entity.WithdrawWaiting;
import com.honeyosori.dogfile.domain.user.identity.BlockIdentity;
import com.honeyosori.dogfile.domain.user.identity.FollowIdentity;
import com.honeyosori.dogfile.domain.user.repository.BlockRepository;
import com.honeyosori.dogfile.domain.user.repository.FollowRepository;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.domain.user.repository.WithdrawWaitingRepository;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.List;

@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;
    private final WithdrawWaitingRepository withdrawWaitingRepository;
    private final BlockRepository blockRepository;
    private final FollowRepository followRepository;
    private final BadgeRepository badgeRepository;
    private final OwnBadgeRepository ownBadgeRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtility jwtUtility;

    @Autowired
    public UserService(UserRepository userRepository, WithdrawWaitingRepository withdrawWaitingRepository, BlockRepository blockRepository, FollowRepository followRepository, BadgeRepository badgeRepository, OwnBadgeRepository ownBadgeRepository, JwtUtility jwtUtility) {
        this.userRepository = userRepository;
        this.withdrawWaitingRepository = withdrawWaitingRepository;
        this.blockRepository = blockRepository;
        this.followRepository = followRepository;
        this.badgeRepository = badgeRepository;
        this.ownBadgeRepository = ownBadgeRepository;
        this.jwtUtility = jwtUtility;
    }

    public BaseResponse<?> register(CreateUserDto createUserDto) {
        String username = createUserDto.username();

        User user = this.userRepository.findUserByUsername(username).orElse(null);

        if (user != null) {
            return new BaseResponse<>(BaseResponseStatus.USERNAME_EXISTS, null);
        }

        User newUser = createUserDto.toUser();
        newUser.setRole(Role.USER);
        newUser.setUserStatus(UserStatus.PUBLIC);
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

        if (updateUserDto.userStatus() != null) {
            user.setUserStatus(updateUserDto.userStatus());
        }

        this.userRepository.save(user);

        return new BaseResponse<>(BaseResponseStatus.UPDATED, updateUserDto);
    }

    public BaseResponse<?> changeUserStatus(UpdateUserStatusDto updateUserStatusDto, String username) {
        User user = this.userRepository.getUserByUsername(username);
        UserStatus userStatus = updateUserStatusDto.userStatus();

        if (userStatus == UserStatus.PUBLIC || userStatus == UserStatus.PRIVATE) {
            user.setUserStatus(userStatus);
            this.userRepository.save(user);

            return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
        }

        return new BaseResponse<>(BaseResponseStatus.REJECTED, "cannot assign value");
    }

    public ResponseEntity<?> login(LoginDto loginDto) {
        String username = loginDto.username();
        String password = loginDto.password();

        User user = this.userRepository.findUserByUsername(username).orElse(null);

        if (user == null) {
            return BaseResponse.getResponseEntity(new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null));
        }

        if (user.getUserStatus() == UserStatus.WITHDRAW_REQUESTED) {
            return BaseResponse.getResponseEntity(new BaseResponse<>(BaseResponseStatus.WITHDRAW_REQUESTED, null));
        }

        if (user.getUserStatus() == UserStatus.WITHDRAW) {
            return BaseResponse.getResponseEntity(new BaseResponse<>(BaseResponseStatus.WITHDRAWN, null));
        }

        if (encoder.matches(password, user.getPassword())) {
            String accessToken = jwtUtility.generateAccessToken(username, user.getPassword());

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

        if (this.withdrawWaitingRepository.existsByUserId(user.getId())) {
            return new BaseResponse<>(BaseResponseStatus.ALREADY_WAITING_FOR_WITHDRAW, null);
        }

        WithdrawWaiting withdrawWaiting = new WithdrawWaiting(user);
        this.withdrawWaitingRepository.save(withdrawWaiting);

        user.setUserStatus(UserStatus.WITHDRAW_REQUESTED);
        this.userRepository.save(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
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

    public BaseResponse<?> getUserInfo(String username) {
        User user = this.userRepository.findUserByUsername(username).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserInfoDto userInfoDto = UserInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userInfoDto);
    }

    public BaseResponse<?> getUserLoginInfo(String username) {
        User user = this.userRepository.findUserByUsername(username).orElse(null);

        if (user == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        UserLoginInfoDto userLoginInfoDto = UserLoginInfoDto.of(user);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, userLoginInfoDto);
    }

    public BaseResponse<?> follow(FollowDto followDto, String username) {
        User user = this.userRepository.getUserByUsername(username);
        String followeeId = followDto.followeeId();
        User followee = this.userRepository.findById(followeeId).orElse(null);

        if (followee == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        FollowIdentity followIdentity = new FollowIdentity(user, followee);
        Follow follow = new Follow(followIdentity);

        this.followRepository.save(follow);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
    }

    public BaseResponse<?> unfollow(FollowDto followDto, String username) {
        User user = this.userRepository.getUserByUsername(username);
        String followeeId = followDto.followeeId();

        Follow follow = this.followRepository.findByFollowIdentity_FollowerIdAndFollowIdentity_FolloweeId(user.getId(), followeeId);

        if (follow == null) {
            return new BaseResponse<>(BaseResponseStatus.NOT_FOLLOWING, null);
        }

        this.followRepository.delete(follow);

        return new BaseResponse<>(BaseResponseStatus.DELETED, null);
    }

    public BaseResponse<?> block(BlockDto blockDto, String username) {
        User user = this.userRepository.getUserByUsername(username);
        String blockeeId = blockDto.blockeeId();
        User blockee = this.userRepository.findById(blockeeId).orElse(null);

        if (blockee == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND, null);
        }

        BlockIdentity blockIdentity = new BlockIdentity(user, blockee);
        Block block = new Block(blockIdentity);

        this.blockRepository.save(block);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, null);
    }

    public BaseResponse<?> unblock(BlockDto blockDto, String username) {
        User user = this.userRepository.getUserByUsername(username);
        String blockeeId = blockDto.blockeeId();

        Block block = this.blockRepository.findBlockByBlockIdentity_BlockerIdAndBlockIdentity_BlockeeId(user.getId(), blockeeId);

        if (block == null) {
            return new BaseResponse<>(BaseResponseStatus.NOT_BLOCKING, null);
        }

        this.blockRepository.delete(block);

        return new BaseResponse<>(BaseResponseStatus.DELETED, null);
    }

    public BaseResponse<?> getFollowees(String username) {
        User user = this.userRepository.getUserByUsername(username);

        List<UserInfoDto> followees = this.followRepository.getFollowsByFollowIdentityFollowerId(user.getId())
                .stream().map(e -> e.getFollowIdentity().getFollowee()).map(UserInfoDto::of).toList();

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, followees);
    }

    public BaseResponse<?> getFollowers(String username) {
        User user = this.userRepository.getUserByUsername(username);

        List<UserInfoDto> followers = this.followRepository.getFollowsByFollowIdentityFolloweeId(user.getId())
                .stream().map(e -> e.getFollowIdentity().getFollower()).map(UserInfoDto::of).toList();

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, followers);
    }

    public BaseResponse<?> getBlockees(String username) {
        User user = this.userRepository.getUserByUsername(username);

        List<UserInfoDto> blockees = this.blockRepository.getBlocksByBlockIdentityBlockerId(user.getId())
                .stream().map(e -> e.getBlockIdentity().getBlockee()).map(UserInfoDto::of).toList();

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, blockees);
    }

    public BaseResponse<?> getBlockers(String username) {
        User user = this.userRepository.getUserByUsername(username);

        List<UserInfoDto> blockers = this.blockRepository.getBlocksByBlockIdentityBlockeeId(user.getId())
                .stream().map(e -> e.getBlockIdentity().getBlocker()).map(UserInfoDto::of).toList();

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, blockers);
    }
}
