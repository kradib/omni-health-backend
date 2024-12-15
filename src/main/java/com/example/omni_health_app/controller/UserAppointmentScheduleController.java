package com.example.omni_health_app.controller;

import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.response.CreateAppointmentResponse;
import com.example.omni_health_app.dto.response.CreateAppointmentResponseData;
import com.example.omni_health_app.dto.response.ResponseMetadata;
import com.example.omni_health_app.dto.response.ResponseWrapper;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.service.UserAppointmentScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/appointment")
public class UserAppointmentScheduleController {

    private final UserAppointmentScheduleService userAppointmentScheduleService;


    @PostMapping
    public ResponseEntity<ResponseWrapper<CreateAppointmentResponseData>> createAppointmentSchedule
            (@RequestBody CreateAppointmentRequest createAppointmentRequest) throws BadRequestException {
        log.info("Receive appointment schedule request {}", createAppointmentRequest);

        final ResponseWrapper<CreateAppointmentResponseData> responseWrapper = CreateAppointmentResponse.builder()
                .data(userAppointmentScheduleService.createAppointmentSchedule(createAppointmentRequest))
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }
}
