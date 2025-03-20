package com.example.omni_health_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static com.example.omni_health_app.util.FileUtils.getFileExtension;

@RequiredArgsConstructor
@Service
public class S3Service implements IDocumentProcessor {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;


    public String uploadFile(final MultipartFile file, final String originalFilename) throws IOException {
        String uniqueFilePath = UUID.randomUUID() + "." + getFileExtension(originalFilename);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFilePath)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        return uniqueFilePath;
    }

    @Override
    public InputStreamResource downloadFile(String filePath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build();

        ResponseBytes<?> response = s3Client.getObjectAsBytes(getObjectRequest);

        return new InputStreamResource(new ByteArrayInputStream(response.asByteArray()));
    }


}
