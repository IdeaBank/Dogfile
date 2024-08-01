package com.honeyosori.dogfile.domain.photo.factory;

import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.S3Client;

public class DependencyFactory {
    private DependencyFactory() {}

    public static S3Client s3Client() {
        return S3Client.builder()
                .httpClientBuilder(ApacheHttpClient.builder())
                .build();
    }
}
