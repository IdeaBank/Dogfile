package com.honeyosori.dogfile.domain.oauth.controller;

import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import com.honeyosori.dogfile.domain.oauth.service.KakaoOAuthService;
import com.honeyosori.dogfile.global.response.dto.GeneralResponse;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
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

    @PostMapping("/kakao/register")
    public GeneralResponse register(@Valid @RequestBody CreateKakaoAccountDto createKakaoAccountDto) {
        return this.kakaoOAuthService.registerUser(createKakaoAccountDto);
    }

    @PostMapping("/kakao/login")
    public ResponseEntity<?> loginWithKakaoAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return this.kakaoOAuthService.loginWithKakao(accessToken);
    }
}
