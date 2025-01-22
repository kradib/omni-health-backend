package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class AppointmentReminderServiceTest {



    @InjectMocks
    private AppointmentReminderService appointmentReminderService;

    @Mock
    private UserAppointmentScheduleRepository appointmentScheduleRepository;

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotifyPendingAppointments() {

        UserDetail userDetail = mock(UserDetail.class);
        UserAppointmentSchedule appointment = new UserAppointmentSchedule();

        appointment.setUserDetail(userDetail);
        appointment.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
        appointment.setAppointmentPlace("Clinic");
        appointment.setDoctorName("Dr. Smith");
        appointment.setUsername("testUser");

        when(userDetail.getEmail()).thenReturn("user@example.com");
        when(userDetail.getFirstGuardianUserId()).thenReturn("guardian1");
        when(userDetail.getSecondGuardianUserId()).thenReturn("guardian2");

        UserDetail guardianDetail1 = new UserDetail();
        UserDetail guardianDetail2 = new UserDetail();


        guardianDetail1.setEmail("guardian1@example.com");
        guardianDetail1.setFirstName("Guardian1");

        guardianDetail2.setEmail("guardian2@example.com");
        guardianDetail2.setFirstName("Guardian2");

        when(userDetailsRepository.findByUsername("guardian1")).thenReturn(guardianDetail1);
        when(userDetailsRepository.findByUsername("guardian2")).thenReturn(guardianDetail2);

        when(appointmentScheduleRepository.findPendingAppointments(any(), any())).thenReturn(List.of(appointment));

        appointmentReminderService.notifyPendingAppointments();

        verify(emailNotificationService, times(1))
                .sendNotification(eq("user@example.com"), eq("Upcoming Appointment Reminder"), any(String.class));

        verify(emailNotificationService, times(1))
                .sendNotification(eq("guardian1@example.com"), eq("Upcoming Appointment Reminder for your dependents"), any(String.class));

        verify(emailNotificationService, times(1))
                .sendNotification(eq("guardian2@example.com"), eq("Upcoming Appointment Reminder for your dependents"), any(String.class));
    }

    @Test
    void testNotifyPendingAppointments_NoAppointments() {
        LocalDateTime fromDate = LocalDateTime.now().plusDays(1);
        LocalDateTime toDate = LocalDateTime.now().plusDays(2);

        when(appointmentScheduleRepository.findPendingAppointments(fromDate, toDate)).thenReturn(Collections.emptyList());

        appointmentReminderService.notifyPendingAppointments();

        verify(emailNotificationService, never()).sendNotification(any(), any(), any());
    }


}
