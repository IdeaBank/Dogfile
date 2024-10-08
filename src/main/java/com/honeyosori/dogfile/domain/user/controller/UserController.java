package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.CreateUserDto;
import com.honeyosori.dogfile.domain.user.dto.LoginDto;
import com.honeyosori.dogfile.domain.user.dto.UpdateUserDto;
import com.honeyosori.dogfile.domain.user.dto.UpdateUserStatusDto;
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
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto updateUserDto, @RequestHeader("X-EMAIL") String email) {
        return BaseResponse.getResponseEntity(this.userService.updateUser(updateUserDto, email));
    }

    @PatchMapping("/status")
    public ResponseEntity<?> updateUserStatus(@Valid @RequestBody UpdateUserStatusDto updateUserStatusDto, @RequestHeader("X-EMAIL") String email) {
        return BaseResponse.getResponseEntity(this.userService.changeUserStatus(updateUserStatusDto, email));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader("X-EMAIL") String email) {
        return BaseResponse.getResponseEntity(this.userService.processWithdrawRequest(email));
    }

    // TODO: change to internal server only
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("X-EMAIL") String email) {
        return BaseResponse.getResponseEntity(this.userService.getUserInfo(email));
    }

    @GetMapping("/login-info")
    public ResponseEntity<?> getLoginInfo(@RequestHeader("X-EMAIL") String email) {
        return BaseResponse.getResponseEntity(this.userService.getUserLoginInfo(email));
    }
}
