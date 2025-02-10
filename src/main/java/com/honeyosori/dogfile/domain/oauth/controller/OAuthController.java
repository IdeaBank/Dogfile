package com.honeyosori.dogfile.domain.oauth.controller;

import com.honeyosori.dogfile.domain.oauth.service.KakaoOAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;

    @Autowired
    public OAuthController(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @PostMapping("/kakao/login")
    public ResponseEntity<?> loginWithKakaoAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return this.kakaoOAuthService.loginWithKakao(accessToken);
    }
}
