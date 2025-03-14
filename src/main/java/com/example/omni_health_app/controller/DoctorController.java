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
