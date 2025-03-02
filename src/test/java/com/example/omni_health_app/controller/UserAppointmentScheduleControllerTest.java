package com.example.omni_health_app.controller;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.dto.request.CancelAppointmentRequest;
import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.request.UpdateAppointmentRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.service.UserAppointmentScheduleService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class UserAppointmentScheduleControllerTest {

    @InjectMocks
    private UserAppointmentScheduleController controller;

    @Mock
    private UserAppointmentScheduleService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    @SneakyThrows
//    void testCreateAppointmentSchedule()  {
//        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
//                .appointmentDateTime(LocalDateTime.now())
//                .appointmentPlace("Clinic")
//                .doctorName("Dr. Smith")
//                .build();
//
//        CreateAppointmentResponseData responseData = CreateAppointmentResponseData.builder()
//                .success(true)
//                .appointmentTime(LocalDateTime.now())
//                .userName("testUser")
//                .build();
//
//        when(service.createAppointmentSchedule(any(), eq(request))).thenReturn(responseData);
//
//        ResponseEntity<ResponseWrapper<CreateAppointmentResponseData>> response = controller.createAppointmentSchedule(request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("testUser", response.getBody().getData().getUserName());
//    }

    @Test
    @SneakyThrows
    void testCancelAppointmentSchedule() {
        CancelAppointmentRequest request = CancelAppointmentRequest.builder()
                .appointmentId(1L)
                .build();

        CancelAppointmentResponseData responseData = CancelAppointmentResponseData.builder()
                .appointmentId(1L)
                .success(true)
                .build();

        when(service.cancelAppointmentSchedule(any(), eq(request))).thenReturn(responseData);

        ResponseEntity<ResponseWrapper<CancelAppointmentResponseData>> response = controller.cancelAppointmentSchedule(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getData().getAppointmentId());
        assertEquals(true, response.getBody().getData().isSuccess());
    }

//    @Test
//    @SneakyThrows
//    void testUpdateAppointmentSchedule() {
//        UpdateAppointmentRequest request = UpdateAppointmentRequest.builder()
//                .appointmentDateTime(LocalDateTime.now().plusDays(1))
//                .appointmentPlace("New Clinic")
//                .doctorName("Dr. John")
//                .build();
//
//        UpdateAppointmentResponseData responseData = UpdateAppointmentResponseData.builder()
//                .appointmentTime(LocalDateTime.now())
//                .userName("testUser")
//                .doctorName("Dr. John")
//                .success(true)
//                .build();
//
//        when(service.updateAppointmentSchedule(any(), eq(1L), eq(request))).thenReturn(responseData);
//
//        ResponseEntity<ResponseWrapper<UpdateAppointmentResponseData>> response = controller.updateAppointmentSchedule(1L, request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
////        assertEquals("testUser", response.getBody().getData().getUserName());
//        assertTrue(response.getBody().getData().isSuccess());
////        assertEquals("Dr. John", response.getBody().getData().getDoctorName());
//    }

    @Test
    @SneakyThrows
    void testGetAllAppointmentSchedule() {
        String startDate = LocalDateTime.now().toString();
        String endDate = LocalDateTime.now().plusDays(7).toString();

        GetAllAppointmentResponseData responseData = GetAllAppointmentResponseData.builder()
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(service.getAllAppointmentSchedule(any(), any(), any(), any(), any())).thenReturn(responseData);

        ResponseEntity<ResponseWrapper<GetAllAppointmentResponseData>> response = controller.getAllAppointmentSchedule(
                startDate, endDate, "created", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getData().getTotalElements());
    }

    @Test
    @SneakyThrows
    void testGetAppointmentSchedule() {
        GetAppointmentResponseData responseData = GetAppointmentResponseData.builder()
                .appointmentSchedule(UserAppointmentSchedule.builder()
                        .id(1L)
                        .build())
                .build();

        when(service.getAppointmentSchedule(any(), eq(1L))).thenReturn(responseData);

        ResponseEntity<ResponseWrapper<GetAppointmentResponseData>> response = controller.getAppointmentSchedule(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getData().getAppointmentSchedule().getId());
    }
}
