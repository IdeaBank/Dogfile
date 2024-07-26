package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.BlockDto;
import com.honeyosori.dogfile.domain.user.dto.CreateUserDto;
import com.honeyosori.dogfile.domain.user.dto.FollowDto;
import com.honeyosori.dogfile.domain.user.dto.UpdateUserDto;
import com.honeyosori.dogfile.domain.user.service.UserService;
import com.honeyosori.dogfile.global.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public BaseResponse<?> createUser(@RequestBody CreateUserDto createUserDto) {
        return this.userService.register(createUserDto);
    }

    @PatchMapping
    public BaseResponse<?> updateUser(@RequestParam Long userId, @RequestBody UpdateUserDto updateUserDto) {
        return this.userService.updateUser(updateUserDto);
    }

    @DeleteMapping
    public BaseResponse<?> deleteUser(@RequestParam Long userId) {
        return this.userService.deleteUser();
    }

    @PostMapping("/follow")
    public BaseResponse<?> followUser(@RequestBody FollowDto followDto) {
        return this.userService.follow(followDto);
    }

    @PostMapping("/block")
    public BaseResponse<?> blockUser(@RequestBody BlockDto blockDto) {
        return this.userService.block(blockDto);
    }
}
