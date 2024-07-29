package com.honeyosori.dogfile.global.controller;

import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GlobalController {
    @GetMapping("/healthz")
    public ResponseEntity<?> checkServerOnline() {
        return BaseResponse.getResponseEntity(new BaseResponse<>(BaseResponseStatus.SUCCESS, null));
    }
}
