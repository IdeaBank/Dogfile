package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.CreateUserDto;
import com.honeyosori.dogfile.domain.user.dto.LoginDto;
import com.honeyosori.dogfile.domain.user.dto.UpdateUserDto;
import com.honeyosori.dogfile.domain.user.service.DogService;
import com.honeyosori.dogfile.domain.user.service.UserService;
import com.honeyosori.dogfile.global.constant.CustomHeader;
import com.honeyosori.dogfile.global.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private UserService userService;
    private DogService dogService;

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(CustomHeader.EMAIL) String email) {
        return this.userService.logout(email);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(CustomHeader.EMAIL) String email, @Valid @CookieValue("refresh_token") String refreshToken) {
        return this.userService.refresh(email, refreshToken);
    }

    @PatchMapping
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto updateUserDto, @RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.updateUser(updateUserDto, email));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.deleteUser(email));
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<?> cancelDeletion(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.cancelDeletion(email));
    }

    @GetMapping("/withdraw")
    public ResponseEntity<?> getWithdrawingUser() {
        return BaseResponse.getResponseEntity(this.userService.getWithdrawingUser());
    }

    // TODO: change to internal server only
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.getUserInfo(email));
    }

    @GetMapping
    public ResponseEntity<?> findUserById(@RequestParam("id") String id) {
        return BaseResponse.getResponseEntity(this.userService.findUserById(id));
    }

    @GetMapping("/by-account-name")
    public ResponseEntity<?> findUserByAccountName(@RequestParam("accountName") String accountName) {
        return BaseResponse.getResponseEntity(this.userService.findUserByAccountName(accountName));
    }

    @GetMapping("by-partial-account-name")
    public ResponseEntity<?> findUserByPartialAccountName(@RequestParam("partialAccountName") String partialAccountName) {
        return BaseResponse.getResponseEntity(this.userService.findUserByPartialAccountName(partialAccountName));
    }

    @GetMapping("/by-email")
    public ResponseEntity<?> findUserByEmail(@RequestParam("email") String email) {
        return BaseResponse.getResponseEntity(this.userService.findUserByEmail(email));
    }

    @GetMapping("/by-phone-number")
    public ResponseEntity<?> findUserByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        return BaseResponse.getResponseEntity(this.userService.findUserByPhoneNumber(phoneNumber));
    }

    @GetMapping("/login-info")
    public ResponseEntity<?> getLoginInfo(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.getUserLoginInfo(email));
    }

    @GetMapping("/get-dogs")
    public ResponseEntity<?> getUserDogs(@RequestParam("email") String email) {
        return BaseResponse.getResponseEntity(this.dogService.getUserDogs(email));
    }
}
