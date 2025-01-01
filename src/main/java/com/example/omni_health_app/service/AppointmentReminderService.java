package com.example.omni_health_app.service;


import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentReminderService {

    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final EmailNotificationService emailNotificationService;
    private final UserDetailsRepository userDetailsRepository;

    public void notifyPendingAppointments() {

        LocalDateTime fromDate = LocalDateTime.now().plusDays(1);
        LocalDateTime toDate = LocalDateTime.now().plusDays(2);
        log.info("Starting to send email notification for all appointments from {} to {}", fromDate, toDate);
        List<UserAppointmentSchedule> pendingAppointments = userAppointmentScheduleRepository.findPendingAppointments(fromDate, toDate);
        log.info("fetched all scheduled appointments {}", pendingAppointments);
        for (UserAppointmentSchedule appointment : pendingAppointments) {
            emailNotificationService.sendNotification(appointment.getUserDetail().getEmail(), "Upcoming Appointment Reminder", buildUserEmailContent(appointment));
            if (appointment.getUserDetail().getFirstGuardianUserId() != null) {
                sendGuardianNotification(appointment, appointment.getUserDetail().getFirstGuardianUserId());
            }
            if (appointment.getUserDetail().getSecondGuardianUserId() != null) {
                sendGuardianNotification(appointment, appointment.getUserDetail().getSecondGuardianUserId());
            }
        }
    }

    private void sendGuardianNotification(UserAppointmentSchedule appointment, String guardianUserName) {
        UserDetail guardianDetail = userDetailsRepository.findByUsername(guardianUserName);
        if (guardianDetail.getEmail() != null) {
            emailNotificationService.sendNotification(
                    guardianDetail.getEmail(), "Upcoming Appointment Reminder for your dependents",
                    buildGuardianEmailContent(appointment, guardianDetail.getFirstName())
            );
        }
    }

    private String buildUserEmailContent(UserAppointmentSchedule appointment) {
        return String.format(
                "Dear %s,\n\nYou have an upcoming appointment with Dr. %s at %s, scheduled on %s.\n\nLocation: %s\n\nBest regards,\nOmni Health App Team",
                appointment.getUsername(),
                appointment.getDoctorName(),
                appointment.getAppointmentDateTime().toLocalTime(),
                appointment.getAppointmentDateTime().toLocalDate(),
                appointment.getAppointmentPlace()
        );
    }

    private String buildGuardianEmailContent(UserAppointmentSchedule appointment, String guardianName) {
        return String.format(
                "Dear %s,\n\nThe user %s has an upcoming appointment with Dr. %s at %s, scheduled on %s.\n\nLocation: %s\n\nBest regards,\nOmni Health App Team",
                guardianName,
                appointment.getUsername(),
                appointment.getDoctorName(),
                appointment.getAppointmentDateTime().toLocalTime(),
                appointment.getAppointmentDateTime().toLocalDate(),
                appointment.getAppointmentPlace()
        );
    }

}
