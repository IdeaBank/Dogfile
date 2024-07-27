package com.honeyosori.dogfile.global.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GlobalController {
    @GetMapping("/healthz")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
