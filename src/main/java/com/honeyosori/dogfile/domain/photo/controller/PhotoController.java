package com.honeyosori.dogfile.domain.photo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/v1/photo")
public class PhotoController {
    @PostMapping()
    public ResponseEntity<?> uploadPhoto(@RequestPart("file") MultipartFile file) {
        Logger logger = LoggerFactory.getLogger(getClass());

        logger.error(String.valueOf(file.getSize()));
        logger.error(file.getName());

        return ResponseEntity.ok().build();
    }
}
