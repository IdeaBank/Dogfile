package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.service.UserService;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import com.honeyosori.dogfile.global.response.BindingResultMessage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public BaseResponse<?> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        return this.userService.register(createUserDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        return this.userService.login(loginDto);
    }

    @PatchMapping
    public BaseResponse<?> updateUser(@Valid @RequestBody UpdateUserDto updateUserDto) {
        return this.userService.updateUser(updateUserDto);
    }

    @DeleteMapping
    public BaseResponse<?> deleteUser() {
        return this.userService.deleteUser();
    }

    @PostMapping("/follow")
    public BaseResponse<?> followUser(@Valid @RequestBody FollowDto followDto) {
        return this.userService.follow(followDto);
    }

    @PostMapping("/block")
    public BaseResponse<?> blockUser(@Valid @RequestBody BlockDto blockDto) {
        return this.userService.block(blockDto);
    }

    @PostMapping("/unfollow")
    public BaseResponse<?> unfollowUser(@Valid @RequestBody FollowDto followDto) {
        return this.userService.unfollow(followDto);
    }

    @PostMapping("/unblock")
    public BaseResponse<?> unblockUser(@Valid @RequestBody BlockDto blockDto) {
        return this.userService.unblock(blockDto);
    }
}
