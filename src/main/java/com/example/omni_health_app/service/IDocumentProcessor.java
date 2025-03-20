package com.example.omni_health_app.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IDocumentProcessor {

    String uploadFile(final MultipartFile file, final String originalFilename) throws IOException;
    InputStreamResource downloadFile(final String filePath);

}
