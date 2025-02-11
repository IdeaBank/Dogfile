package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.*;
import com.honeyosori.dogfile.domain.user.service.UserService;
import com.honeyosori.dogfile.global.constant.CustomHeader;
import com.honeyosori.dogfile.global.response.dto.BaseResponse;
import com.honeyosori.dogfile.global.response.dto.GeneralResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public CreateUserDto createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        return this.userService.register(createUserDto);
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        return this.userService.login(loginDto);
    }

    @PostMapping("/user/logout")
    public GeneralResponse logout(@RequestHeader(CustomHeader.EMAIL) String email) {
        return this.userService.logout(email);
    }

    @PostMapping("/user/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(CustomHeader.EMAIL) String email, @Valid @CookieValue("refresh_token") String refreshToken) {
        return this.userService.refresh(email, refreshToken);
    }

    @PatchMapping("/user")
    public UpdateUserDto updateUser(@RequestBody UpdateUserDto updateUserDto, @RequestHeader(CustomHeader.EMAIL) String email) {
        return this.userService.updateUser(updateUserDto, email);
    }

    @DeleteMapping("/user")
    public GeneralResponse deleteUser(@RequestHeader(CustomHeader.EMAIL) String email) {
        return this.userService.deleteUser(email);
    }

    @DeleteMapping("/user/cancel")
    public GeneralResponse cancelDeletion(@RequestHeader(CustomHeader.EMAIL) String email) {
        return this.userService.cancelDeletion(email);
    }

    @GetMapping("/user/withdraw")
    public List<UserInfoDto> getWithdrawingUser() {
        return this.userService.getWithdrawingUser();
    }

    // TODO: change to internal server only
    @GetMapping("/user/info")
    public UserInfoDto getUserInfo(@RequestHeader(CustomHeader.EMAIL) String email) {
        return this.userService.getUserInfo(email);
    }

    @GetMapping("/user/{id}")
    public UserInfoDto findUserById(@PathVariable String id) {
        return this.userService.findUserById(id);
    }

    @GetMapping("/user/by-account-name/{accountName}")
    public UserInfoDto findUserByAccountName(@PathVariable String accountName) {
        return this.userService.findUserByAccountName(accountName);
    }

    @GetMapping("/users/by-account-name")
    public List<UserInfoDto> findUserByPartialAccountName(@RequestParam("query") String query) {
        return this.userService.findUserByPartialAccountName(query);
    }

    @GetMapping("/user/by-email/{email}")
    public UserInfoDto findUserByEmail(@PathVariable("email") String email) {
        return this.userService.findUserByEmail(email);
    }

    @GetMapping("/user/by-phone-number/{phoneNumber}")
    public UserInfoDto findUserByPhoneNumber(@PathVariable String phoneNumber) {
        return this.userService.findUserByPhoneNumber(phoneNumber);
    }

    @GetMapping("/user/login-info")
    public UserLoginInfoDto getLoginInfo(@RequestHeader(CustomHeader.EMAIL) String email) {
        return this.userService.getUserLoginInfo(email);
    }
}
