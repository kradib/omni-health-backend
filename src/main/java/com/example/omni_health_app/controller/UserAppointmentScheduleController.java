package com.example.omni_health_app.controller;

import com.example.omni_health_app.dto.request.CancelAppointmentRequest;
import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.request.UpdateAppointmentRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.service.UserAppointmentScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.example.omni_health_app.util.UserNameUtil.getCurrentUsername;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/appointment")
public class UserAppointmentScheduleController {

    private final UserAppointmentScheduleService userAppointmentScheduleService;


    @PostMapping
    public ResponseEntity<ResponseWrapper<CreateAppointmentResponseData>> createAppointmentSchedule
            (@RequestBody CreateAppointmentRequest createAppointmentRequest) throws BadRequestException {
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


    //TODO: paginate this end point
    @GetMapping
    public ResponseEntity<ResponseWrapper<GetAllAppointmentResponseData>> getAllAppointmentSchedule(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) throws BadRequestException {
        final String userName = getCurrentUsername();
        log.info("Receive get all appointments from {} to {} for {}", startDate, endDate, userName);

        final ResponseWrapper<GetAllAppointmentResponseData> responseWrapper = GetAllAppointmentResponse.builder()
                .data(userAppointmentScheduleService.getAllAppointmentSchedule(userName, LocalDateTime.parse(startDate),
                        LocalDateTime.parse(endDate)))
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
