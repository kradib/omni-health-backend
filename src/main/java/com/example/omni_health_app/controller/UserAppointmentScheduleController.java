package com.example.omni_health_app.controller;

import com.example.omni_health_app.dto.request.CancelAppointmentRequest;
import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.request.UpdateAppointmentRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.AppointmentAlreadyExistsException;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.service.UserAppointmentScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.omni_health_app.util.UserNameUtil.getCurrentUsername;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/appointment")
public class UserAppointmentScheduleController {

    private final UserAppointmentScheduleService userAppointmentScheduleService;


    @PostMapping
    @PreAuthorize("hasRole('ROLE_PATIENT')") 
    public ResponseEntity<ResponseWrapper<CreateAppointmentResponseData>> createAppointmentSchedule
            (@RequestBody CreateAppointmentRequest createAppointmentRequest) throws BadRequestException, AppointmentAlreadyExistsException {
        final String userName = getCurrentUsername();
        log.info("Receive appointment schedule request {} for {}", createAppointmentRequest, userName);

        final ResponseWrapper<CreateAppointmentResponseData> responseWrapper = CreateAppointmentResponse.builder()
                .data(userAppointmentScheduleService.createAppointmentSchedule(userName, createAppointmentRequest))
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }

    @GetMapping("/slots")
    public ResponseEntity<ResponseWrapper<GetAllAppointmentSlotsResponseData>> getAllAppointmentSlots
            (@RequestParam("doctorId") long doctorId, @RequestParam("appointmentDate") LocalDate appointmentDate) throws BadRequestException {
        log.info("Receive getAppointmentSlots schedule request for doctor {} for {}", doctorId,
                appointmentDate);
        final ResponseWrapper<GetAllAppointmentSlotsResponseData> responseWrapper = GetAllAppointmentSlotsResponse.builder()
                .data(GetAllAppointmentSlotsResponseData.builder()
                        .appointmentSlotAvailableList(userAppointmentScheduleService.getAppointmentSlotsPerDoctor(doctorId, appointmentDate))
                        .build()
                )
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }

    @DeleteMapping
    public ResponseEntity<ResponseWrapper<CancelAppointmentResponseData>> cancelAppointmentSchedule
            (@RequestBody CancelAppointmentRequest cancelAppointmentRequest) throws BadRequestException {
        final String userName = getCurrentUsername();
        log.info("Receive appointment cancel request {} for {}", cancelAppointmentRequest, userName);

        final ResponseWrapper<CancelAppointmentResponseData> responseWrapper = CancelAppointmentResponse.builder()
                .data(userAppointmentScheduleService.cancelAppointmentSchedule(userName, cancelAppointmentRequest))
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }


    @PatchMapping("/{appointmentId}")
    public ResponseEntity<ResponseWrapper<UpdateAppointmentResponseData>> updateAppointmentSchedule(
            @PathVariable("appointmentId") Long appointmentId,
            @RequestBody UpdateAppointmentRequest updateAppointmentRequest) throws BadRequestException {
        final String userName = getCurrentUsername();
        log.info("Receive appointment update request {} for {} on {}", updateAppointmentRequest, userName,
                appointmentId);

        final ResponseWrapper<UpdateAppointmentResponseData> responseWrapper = UpdateAppointmentResponse.builder()
                .data(userAppointmentScheduleService.updateAppointmentSchedule(userName, appointmentId,
                        updateAppointmentRequest))
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }


    @GetMapping
    public ResponseEntity<ResponseWrapper<GetAllAppointmentResponseData>> getAllAppointmentSchedule(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws BadRequestException {
        final String userName = getCurrentUsername();
        log.info("Receive get all appointments from {} to {} for {} with status {}", startDate, endDate, userName, status);
        final Pageable pageable = PageRequest.of(page, size);
        final ResponseWrapper<GetAllAppointmentResponseData> responseWrapper = GetAllAppointmentResponse.builder()
                .data(userAppointmentScheduleService.getAllAppointmentSchedule(userName,
                        startDate == null? null :LocalDateTime.parse(startDate),
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

    @GetMapping("/dependents")
    public ResponseEntity<ResponseWrapper<GetAllAppointmentResponseData>> getAllAppointmentScheduleForDependents(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws BadRequestException {
        final String userName = getCurrentUsername();
        log.info("Receive get all dependent appointments from {} to {} for {} with status {}", startDate, endDate,
                userName, status);
        final Pageable pageable = PageRequest.of(page, size);
        final ResponseWrapper<GetAllAppointmentResponseData> responseWrapper = GetAllAppointmentResponse.builder()
                .data(userAppointmentScheduleService.getAllAppointmentScheduleForDependents(userName,
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

    @GetMapping("/{appointmentId}")
    public ResponseEntity<ResponseWrapper<GetAppointmentResponseData>> getAppointmentSchedule(
            @PathVariable("appointmentId") Long appointmentId) throws BadRequestException {
        final String userName = getCurrentUsername();
        log.info("Receive get appointment request with id {} for {}", appointmentId, userName);

        final ResponseWrapper<GetAppointmentResponseData> responseWrapper = GetAppointmentResponse.builder()
                .data(userAppointmentScheduleService.getAppointmentSchedule(userName, appointmentId))
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }

}
