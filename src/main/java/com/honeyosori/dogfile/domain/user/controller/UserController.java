package com.honeyosori.dogfile.domain.user.controller;

import com.honeyosori.dogfile.domain.user.dto.CreateUserDto;
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
    public ResponseEntity<?> updateUser(@RequestParam Long userId, @RequestBody UpdateUserDto updateUserDto) throws ClassNotFoundException{
        this.userService.updateUser(userId, updateUserDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestParam Long userId) throws ClassNotFoundException {
        this.userService.deleteUser(userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
