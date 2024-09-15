package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.service.UserService;
import com.honeyosori.dogfile.global.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto updateUserDto, @RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.updateUser(updateUserDto, username));
    }

    @PatchMapping("/status")
    public ResponseEntity<?> updateUserStatus(@Valid @RequestBody UpdateUserStatusDto updateUserStatusDto, @RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.changeUserStatus(updateUserStatusDto, username));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.deleteUser(username));
    }

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@Valid @RequestBody FollowDto followDto, @RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.follow(followDto, username));
    }

    @PostMapping("/block")
    public ResponseEntity<?> blockUser(@Valid @RequestBody BlockDto blockDto, @RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.block(blockDto, username));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@Valid @RequestBody FollowDto followDto, @RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.unfollow(followDto, username));
    }

    @PostMapping("/unblock")
    public ResponseEntity<?> unblockUser(@Valid @RequestBody BlockDto blockDto, @RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.unblock(blockDto, username));
    }

    @GetMapping("/follower")
    public ResponseEntity<?> getFollowers(@RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.getFollowers(username));
    }

    @GetMapping("/followee")
    public ResponseEntity<?> getFollowees(@RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.getFollowees(username));
    }

    @GetMapping("/blocker")
    public ResponseEntity<?> getBlockers(@RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.getBlockers(username));
    }

    @GetMapping("/blockee")
    public ResponseEntity<?> getBlockees(@RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.getBlockees(username));
    }

    @PutMapping("/add-badge")
    public ResponseEntity<?> addBadge(@Valid @RequestBody AddBadgeDto addBadgeDto, @RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.addBadge(addBadgeDto, username));
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.getUserInfo(username));
    }

    @GetMapping("/login-info")
    public ResponseEntity<?> getLoginInfo(@RequestHeader("X-USERNAME") String username) {
        return BaseResponse.getResponseEntity(this.userService.getUserLoginInfo(username));
    }
}
