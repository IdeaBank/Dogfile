package com.honeyosori.dogfile.domain.oauth.controller;

import com.honeyosori.dogfile.domain.oauth.service.KakaoOAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/oauth")
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;

    @Autowired
    public OAuthController(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @GetMapping("/kakao_code")
    public ResponseEntity<?> receiveKakaoCode(HttpServletRequest request) {
        return this.kakaoOAuthService.receiveKakaoCode(request);
    }

    @GetMapping("/kakao_access_token")
    public ResponseEntity<?> receiveKakaoAccessToken(HttpServletRequest request) {
        return this.kakaoOAuthService.receiveAccessToken(request);
    }
}
