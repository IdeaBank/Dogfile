package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.service.UserService;
import com.honeyosori.dogfile.global.response.BaseResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        return BaseResponse.getResponseEntity(this.userService.register(createUserDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        return this.userService.login(loginDto);
    }

    @PatchMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserDto updateUserDto, Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.updateUser(updateUserDto, authentication.getName()));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.deleteUser(authentication.getName()));
    }

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@Valid @RequestBody FollowDto followDto, Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.follow(followDto, authentication.getName()));
    }

    @PostMapping("/block")
    public ResponseEntity<?> blockUser(@Valid @RequestBody BlockDto blockDto, Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.block(blockDto, authentication.getName()));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@Valid @RequestBody FollowDto followDto, Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.unfollow(followDto, authentication.getName()));
    }

    @PostMapping("/unblock")
    public ResponseEntity<?> unblockUser(@Valid @RequestBody BlockDto blockDto, Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.unblock(blockDto, authentication.getName()));
    }

    @GetMapping("/follower")
    public ResponseEntity<?> getFollowers(Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.getFollowers(authentication.getName()));
    }

    @GetMapping("/followee")
    public ResponseEntity<?> getFollowees(Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.getFollowees(authentication.getName()));
    }

    @GetMapping("/blocker")
    public ResponseEntity<?> getBlockers(Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.getBlockers(authentication.getName()));
    }

    @GetMapping("/blockee")
    public ResponseEntity<?> getBlockees(Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.getBlockees(authentication.getName()));
    }

    @PutMapping("/add-badge")
    public ResponseEntity<?> addBadge(@Valid @RequestBody AddBadgeDto addBadgeDto, Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.addBadge(addBadgeDto, authentication.getName()));
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        return BaseResponse.getResponseEntity(this.userService.getUserInfo(authentication.getName()));
    }
}
