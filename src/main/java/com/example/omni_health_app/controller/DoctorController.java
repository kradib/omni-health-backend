package com.example.omni_health_app.controller;

import com.example.omni_health_app.domain.entity.DocumentEntity;
import com.example.omni_health_app.dto.request.UpdateAppointmentStatusRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.service.DoctorService;
import com.example.omni_health_app.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.example.omni_health_app.util.UserNameUtil.getCurrentUsername;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final DocumentService documentService;

    @PatchMapping("/appointments/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ResponseWrapper<UpdateAppointmentResponseData>> updateAppointmentSchedule(
            @PathVariable("appointmentId") Long appointmentId,
            @RequestBody UpdateAppointmentStatusRequest updateAppointmentStatusRequest) throws BadRequestException {
        final String userName = getCurrentUsername();
        log.info("Receive appointment update request {} for {} on {}", updateAppointmentStatusRequest, userName,
                appointmentId);

        final ResponseWrapper<UpdateAppointmentResponseData> responseWrapper = UpdateAppointmentResponse.builder()
                .data(doctorService.updateAppointmentScheduleStatus(userName, appointmentId,
                updateAppointmentStatusRequest))
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }

    @GetMapping("/appointments/{appointmentId}/documents")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ResponseWrapper<GetAllDocumentsResponseData>> getAppointmentDocuments(
            @PathVariable("appointmentId") Long appointmentId) throws BadRequestException, UserAuthException {
        final String userName = getCurrentUsername();
        log.info("Received get all documents for appointment {} for doctor {}", appointmentId, userName);

        final ResponseWrapper<GetAllDocumentsResponseData> responseWrapper = GetAllDocumentsResponse.builder()
                .data(GetAllDocumentsResponseData.builder()
                        .success(true)
                        .documentMetaData(doctorService.getAllDocuments(appointmentId, userName))
                        .build())
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }

    @GetMapping("/appointments/{appointmentId}/documents/{documentId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<InputStreamResource> getAppointmentDocuments(
            @PathVariable("appointmentId") Long appointmentId,  @PathVariable("documentId") Long documentId) throws BadRequestException, UserAuthException, IOException {
        final String userName = getCurrentUsername();
        final DocumentEntity documentEntity = documentService.getDocument(userName, appointmentId, documentId);
        log.info("Received get document for appointment {} for doctor {}", appointmentId, userName);

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

    @GetMapping
    public ResponseEntity<ResponseWrapper<ListDoctorsResponseData>> listDoctors() {
        log.info("Received request to fetch doctor list");
        final ResponseWrapper<ListDoctorsResponseData> responseWrapper = ListDoctorsResponse.builder()
                .data(ListDoctorsResponseData.builder()
                        .doctorDetails(doctorService.listDoctors())
                        .build())
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

}
