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
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto updateUserDto, @RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.updateUser(updateUserDto, email));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.deleteUser(email));
    }

    // TODO: change to internal server only
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.getUserInfo(email));
    }

    @GetMapping("/find")
    public ResponseEntity<?> findUser(@RequestParam("email") String email) {
        return BaseResponse.getResponseEntity(this.userService.getUserInfo(email));
    }

    @GetMapping("/find-all")
    public ResponseEntity<?> findAllUser(@RequestParam("email") String email) {
        return BaseResponse.getResponseEntity(this.userService.findAllUser(email));
    }

    @GetMapping("/login-info")
    public ResponseEntity<?> getLoginInfo(@RequestHeader(CustomHeader.EMAIL) String email) {
        return BaseResponse.getResponseEntity(this.userService.getUserLoginInfo(email));
    }
}
