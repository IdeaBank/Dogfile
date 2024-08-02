package com.honeyosori.dogfile.domain.upload.service;

import com.honeyosori.dogfile.domain.upload.dto.UploadDto;
import com.honeyosori.dogfile.domain.upload.factory.DependencyFactory;
import com.honeyosori.dogfile.global.constant.AwsConstant;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

@Service
public class UploadService {
    private String bucketName;
    private S3Client s3Client;

    public UploadService(
            @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.s3Client = DependencyFactory.s3Client();
        this.bucketName = bucketName;

        createBucket(s3Client, this.bucketName);
    }

    public ResponseEntity<?> sendRequest(UploadDto uploadDto) throws Exception {
        MultipartFile multipartFile = uploadDto.file();
        File file = new File(UUID.randomUUID() + multipartFile.getOriginalFilename());

        System.out.println(multipartFile.getSize());

        if(multipartFile.getSize() > AwsConstant.MAX_UPLOAD_FILE_SIZE) {
            return BaseResponse.getResponseEntity(new BaseResponse<>(BaseResponseStatus.DATA_TOO_BIG, String.format("%.2f", (float) multipartFile.getSize() / MB) + "MB"));
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(multipartFile.getBytes());
        }

        this.s3Client = DependencyFactory.s3Client();

        S3AsyncClient s3AsyncClient = S3AsyncClient.crtBuilder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .targetThroughputInGbps(20.0)
                .minimumPartSizeInBytes(8 * MB)
                .build();

        S3TransferManager transferManager = S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();

        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                .putObjectRequest(b -> b.bucket(bucketName).key(file.getName()))
                .addTransferListener(LoggingTransferListener.create())
                .source(file.toPath())
                .build();

        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);

        CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
        System.out.println(uploadResult.response());
        s3Client.close();

        file.delete();

        return BaseResponse.getResponseEntity(new BaseResponse<>(BaseResponseStatus.SUCCESS, "https://honeybadgerdogfile.s3.us-east-2.amazonaws.com/" + file.getName()));
    }

    public static void createBucket(S3Client s3Client, String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest
                    .builder()
                    .bucket(bucketName)
                    .build());
            System.out.println("Creating bucket: " + bucketName);
            s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            System.out.println(bucketName + " is ready.");
        } catch (S3Exception e) {
            System.err.println(bucketName + " already exists.");
        }
    }
}
