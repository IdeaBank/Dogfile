package com.honeyosori.dogfile.domain.upload.controller;

import com.honeyosori.dogfile.domain.upload.dto.UploadDto;
import com.honeyosori.dogfile.domain.upload.service.UploadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/upload")
public class UploadController {
    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    public ResponseEntity<?> uploadPhoto(@Valid UploadDto uploadDto) throws Exception {
        return this.uploadService.sendRequest(uploadDto);
    }
}
