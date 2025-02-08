package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.CreateUserDto;
import com.honeyosori.dogfile.domain.user.dto.LoginDto;
import com.honeyosori.dogfile.domain.user.dto.UpdateUserDto;
import com.honeyosori.dogfile.domain.user.service.UserService;
import com.honeyosori.dogfile.global.constant.CustomHeader;
import com.honeyosori.dogfile.global.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        return BaseResponse.getResponseEntity(this.userService.register(createUserDto));
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        return this.userService.login(loginDto);
    }

    @PostMapping("/user/logout")
    public ResponseEntity<?> logout(@RequestHeader(CustomHeader.EMAIL) String email) {
        return this.userService.logout(email);
    }

    @PostMapping("/user/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(CustomHeader.EMAIL) String email, @Valid @CookieValue("refresh_token") String refreshToken) {
        return this.userService.refresh(email, refreshToken);
    }

    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto updateUserDto, @RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.updateUser(updateUserDto, email));
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.deleteUser(email));
    }

    @DeleteMapping("/user/cancel")
    public ResponseEntity<?> cancelDeletion(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.cancelDeletion(email));
    }

    @GetMapping("/user/withdraw")
    public ResponseEntity<?> getWithdrawingUser() {
        return BaseResponse.getResponseEntity(this.userService.getWithdrawingUser());
    }

    // TODO: change to internal server only
    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.getUserInfo(email));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> findUserById(@PathVariable String id) {
        return BaseResponse.getResponseEntity(this.userService.findUserById(id));
    }

    @GetMapping("/user/by-account-name/{accountName}")
    public ResponseEntity<?> findUserByAccountName(@PathVariable String accountName) {
        return BaseResponse.getResponseEntity(this.userService.findUserByAccountName(accountName));
    }

    @GetMapping("/users/by-account-name")
    public ResponseEntity<?> findUserByPartialAccountName(@RequestParam("query") String query) {
        return BaseResponse.getResponseEntity(this.userService.findUserByPartialAccountName(query));
    }

    @GetMapping("/user/by-email/{email}")
    public ResponseEntity<?> findUserByEmail(@PathVariable("email") String email) {
        return BaseResponse.getResponseEntity(this.userService.findUserByEmail(email));
    }

    @GetMapping("/user/by-phone-number/{phoneNumber}")
    public ResponseEntity<?> findUserByPhoneNumber(@PathVariable String phoneNumber) {
        return BaseResponse.getResponseEntity(this.userService.findUserByPhoneNumber(phoneNumber));
    }

    @GetMapping("/user/login-info")
    public ResponseEntity<?> getLoginInfo(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.getUserLoginInfo(email));
    }
}
