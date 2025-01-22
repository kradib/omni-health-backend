package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.model.AppointmentStatus;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.request.CancelAppointmentRequest;
import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.request.UpdateAppointmentRequest;
import com.example.omni_health_app.dto.response.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UserAppointmentScheduleServiceTest {

    @InjectMocks
    private UserAppointmentScheduleService service;

    @Mock
    private UserAppointmentScheduleRepository scheduleRepository;

    @Mock
    private UserAuthRepository authRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    void testCreateAppointmentSchedule() {
        String userName = "testUser";
        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                .appointmentDateTime(LocalDateTime.now())
                .appointmentPlace("Clinic")
                .doctorName("Dr. Smith")
                .build();

        UserAuth userAuth = mock(UserAuth.class);
        when(authRepository.findByUsername(eq(userName))).thenReturn(Optional.of(userAuth));

        UserAppointmentSchedule savedSchedule = UserAppointmentSchedule.builder()
                .appointmentDateTime(request.getAppointmentDateTime())
                .appointmentPlace(request.getAppointmentPlace())
                .doctorName(request.getDoctorName())
                .username(userName)
                .status(AppointmentStatus.CREATED.getStatus())
                .build();

        when(scheduleRepository.save(any(UserAppointmentSchedule.class))).thenReturn(savedSchedule);

        CreateAppointmentResponseData response = service.createAppointmentSchedule(userName, request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(userName, response.getUserName());
        assertEquals(request.getAppointmentDateTime(), response.getAppointmentTime());
    }

    @Test
    @SneakyThrows
    void testCancelAppointmentSchedule() {
        String userName = "testUser";
        CancelAppointmentRequest request = CancelAppointmentRequest.builder()
                .appointmentId(1L)
                .build();

        UserAuth userAuth = mock(UserAuth.class);
        UserAppointmentSchedule schedule = mock(UserAppointmentSchedule.class);

        when(authRepository.findByUsername(eq(userName))).thenReturn(Optional.of(userAuth));
        when(scheduleRepository.findById(eq(request.getAppointmentId()))).thenReturn(Optional.of(schedule));
        when(schedule.getUsername()).thenReturn(userName);

        CancelAppointmentResponseData response = service.cancelAppointmentSchedule(userName, request);

        verify(schedule).setStatus(AppointmentStatus.CANCELLED.getStatus());
        verify(scheduleRepository).save(schedule);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(request.getAppointmentId(), response.getAppointmentId());
    }

    @Test
    @SneakyThrows
    void testUpdateAppointmentSchedule() {
        String userName = "testUser";
        Long appointmentId = 1L;
        UpdateAppointmentRequest request = UpdateAppointmentRequest.builder()
                .appointmentDateTime(LocalDateTime.now().plusDays(1))
                .appointmentPlace("New Clinic")
                .doctorName("Dr. Doe")
                .build();

        UserAuth userAuth = mock(UserAuth.class);
        UserAppointmentSchedule existingSchedule = mock(UserAppointmentSchedule.class);
        UserAppointmentSchedule updatedSchedule = UserAppointmentSchedule.builder()
                .appointmentDateTime(request.getAppointmentDateTime())
                .appointmentPlace(request.getAppointmentPlace())
                .doctorName(request.getDoctorName())
                .username(userName)
                .status(AppointmentStatus.UPDATED.getStatus())
                .build();

        when(authRepository.findByUsername(eq(userName))).thenReturn(Optional.of(userAuth));
        when(scheduleRepository.findById(eq(appointmentId))).thenReturn(Optional.of(existingSchedule));
        when(existingSchedule.getUsername()).thenReturn(userName);
        when(scheduleRepository.save(any(UserAppointmentSchedule.class))).thenReturn(updatedSchedule);

        UpdateAppointmentResponseData response = service.updateAppointmentSchedule(userName, appointmentId, request);

        verify(existingSchedule).setAppointmentDateTime(request.getAppointmentDateTime());
        verify(existingSchedule).setAppointmentPlace(request.getAppointmentPlace());
        verify(existingSchedule).setDoctorName(request.getDoctorName());
        verify(existingSchedule).setStatus(AppointmentStatus.UPDATED.getStatus());
        verify(scheduleRepository).save(existingSchedule);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(request.getAppointmentDateTime(), response.getAppointmentTime());
        assertEquals(request.getDoctorName(), response.getDoctorName());
    }

    @Test
    @SneakyThrows
    void testGetAllAppointmentSchedule() {
        String userName = "testUser";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        PageRequest pageable = PageRequest.of(0, 10);

        UserAuth userAuth = mock(UserAuth.class);
        UserAppointmentSchedule schedule = mock(UserAppointmentSchedule.class);
        UserDetail userDetail = mock(UserDetail.class);

        when(schedule.getUsername()).thenReturn(userName);
        when(schedule.getUserDetail()).thenReturn(userDetail);
        when(userDetail.getFirstGuardianUserId()).thenReturn("guardianUser");

        when(authRepository.findByUsername(eq(userName))).thenReturn(Optional.of(userAuth));
        when(scheduleRepository.findAppointmentsByUserAndDateRange(eq(userName), eq(startDate), eq(endDate), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(schedule)));

        GetAllAppointmentResponseData response = service.getAllAppointmentSchedule(userName, startDate, endDate, pageable);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(1, response.getOwnAppointments().size());
        assertEquals(userName, response.getOwnAppointments().get(0).getUsername());
    }

    @Test
    @SneakyThrows
    void testGetAppointmentSchedule() {
        String userName = "testUser";
        Long appointmentId = 1L;

        UserAuth userAuth = mock(UserAuth.class);
        UserAppointmentSchedule schedule = mock(UserAppointmentSchedule.class);

        when(authRepository.findByUsername(eq(userName))).thenReturn(Optional.of(userAuth));
        when(scheduleRepository.findById(eq(appointmentId))).thenReturn(Optional.of(schedule));

        GetAppointmentResponseData response = service.getAppointmentSchedule(userName, appointmentId);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(schedule, response.getAppointmentSchedule());
    }


}
