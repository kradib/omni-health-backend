package com.example.omni_health_app.controller;

import com.example.omni_health_app.dto.request.UpdateAppointmentStatusRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.example.omni_health_app.util.UserNameUtil.getCurrentUsername;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    @PatchMapping("/appointments/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
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

    @GetMapping("/appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ResponseWrapper<GetAllAppointmentResponseData>> getAppointments(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", defaultValue = "created") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws BadRequestException {
        final String userName = getCurrentUsername();
        log.info("Receive get all dependent appointments from {} to {} for {} with status {}", startDate, endDate,
                userName, status);
        final Pageable pageable = PageRequest.of(page, size);

        final ResponseWrapper<GetAllAppointmentResponseData> responseWrapper = GetAllAppointmentResponse.builder()
                .data(doctorService.getAllAppointmentSchedule(userName,
                        startDate == null? null : LocalDateTime.parse(startDate),
                        endDate == null ? null : LocalDateTime.parse(endDate),
                        status,
                        pageable))
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
