package com.honeyosori.dogfile.domain.upload.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public record UploadDto(@NotNull @RequestPart("file") MultipartFile file) {
}
