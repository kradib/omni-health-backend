package com.example.omni_health_app.controller;


import com.example.omni_health_app.domain.entity.DocumentEntity;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.omni_health_app.util.UserNameUtil.getCurrentUsername;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentUploadController {


    private final DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<ResponseWrapper<UploadDocumentResponseData>> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("documentName") String documentName)
            throws BadRequestException, IOException {

        final String userName = getCurrentUsername();
        final ResponseWrapper<UploadDocumentResponseData> responseWrapper = UploadDocumentResponse.builder()
                .data(UploadDocumentResponseData.builder()
                        .documentMetadata(documentService.uploadFile(file, userName, documentName))
                        .build())
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<ResponseWrapper<GetAllDocumentsResponseData>> getDocuments() throws BadRequestException {

        final String userName = getCurrentUsername();
        final ResponseWrapper<GetAllDocumentsResponseData> responseWrapper = GetAllDocumentsResponse.builder()
                .data(GetAllDocumentsResponseData.builder()
                        .success(true)
                        .documentMetaData(documentService.getAllDocuments(userName))
                        .build())
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<InputStreamResource> downloadDocument(@PathVariable("id") final int id) throws BadRequestException,
            IOException, UserAuthException {

        final String userName = getCurrentUsername();
        final DocumentEntity documentEntity = documentService.getDocument(userName, id);

        Path filePath = Paths.get(documentEntity.getFilePath());
        File file = filePath.toFile();
        if (!file.exists() || !file.canRead()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found or not readable");
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
    }






}
