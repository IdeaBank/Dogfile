package com.honeyosori.dogfile.domain.photo.controller;

import com.honeyosori.dogfile.domain.photo.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/photo")
public class PhotoController {
    private final PhotoService photoService;

    public PhotoController() {
        this.photoService = new PhotoService();
    }

    @PostMapping()
    public ResponseEntity<?> uploadPhoto(@RequestPart("file") MultipartFile file) {
        Logger logger = LoggerFactory.getLogger(getClass());

        logger.error(String.valueOf(file.getSize()));
        logger.error(file.getName());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
    public ResponseEntity<?> test() {
        this.photoService.sendRequest();
        return ResponseEntity.ok().build();
    }
}
