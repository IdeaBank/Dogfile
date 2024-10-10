package com.honeyosori.dogfile.domain.oauth.controller;

import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import com.honeyosori.dogfile.domain.oauth.service.KakaoOAuthService;
import com.honeyosori.dogfile.global.constant.CustomHeader;
import com.honeyosori.dogfile.global.constant.PayloadData;
import com.honeyosori.dogfile.global.constant.RequestParameter;
import com.honeyosori.dogfile.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/oauth")
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;

    @Autowired
    public OAuthController(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @GetMapping("/kakao/oauth")
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        return this.kakaoOAuthService.authenticate(request);
    }

    @PostMapping("/kakao/register")
    public BaseResponse<?> registerKakaoAccount(@RequestHeader(CustomHeader.EMAIL) String email, @Valid @RequestBody CreateKakaoAccountDto createKakaoAccountDto) {
        return this.kakaoOAuthService.registerKakaoAccount(email, createKakaoAccountDto);
    }

    @PostMapping("/kakao/login")
    public ResponseEntity<?> loginWithKakaoAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return this.kakaoOAuthService.loginWithKakao(accessToken);
    }
}
