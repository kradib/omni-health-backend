package com.example.omni_health_app.controller;


import com.example.omni_health_app.dto.request.UpdateAppointmentStatusRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.BadRequestException;

import com.example.omni_health_app.service.DoctorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ResponseWrapper<ListDoctorsResponseData>> listDoctors(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                                                @RequestParam(value = "name", required = false) String name) {
        log.info("Received request to fetch doctor list");
        final ResponseWrapper<ListDoctorsResponseData> responseWrapper = ListDoctorsResponse.builder()
                .data(doctorService.listDoctors(page, name, size))
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

}
