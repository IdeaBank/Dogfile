package com.honeyosori.dogfile.domain.oauth.controller;

import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import com.honeyosori.dogfile.domain.oauth.service.KakaoOAuthService;
import com.honeyosori.dogfile.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/kakao_code")
    public ResponseEntity<?> receiveKakaoCode(HttpServletRequest request) {
        return this.kakaoOAuthService.receiveKakaoCode(request);
    }

    @GetMapping("/kakao_access_token")
    public ResponseEntity<?> receiveKakaoAccessToken(HttpServletRequest request) {
        return this.kakaoOAuthService.receiveAccessToken(request);
    }

    @PostMapping("/kakao_register")
    public BaseResponse<?> registerKakaoAccount(@RequestHeader("X-EMAIL") String email, @Valid @RequestBody CreateKakaoAccountDto createKakaoAccountDto) {
        return this.kakaoOAuthService.registerNewAccount(email, createKakaoAccountDto);
    }
}
