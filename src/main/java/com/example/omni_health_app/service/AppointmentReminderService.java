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
                """
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                        }
                        .header {
                            background-color: #4CAF50;
                            color: white;
                            padding: 10px 20px;
                            text-align: center;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                        }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            font-size: 14px;
                            color: #777777;
                        }
                        .details {
                            margin: 10px 0;
                            padding: 10px;
                            background-color: #f9f9f9;
                            border: 1px solid #dddddd;
                            border-radius: 5px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">Appointment Reminder</div>
                    <div class="content">
                        <p>Dear <strong>%s</strong>,</p>
                        <p>You have an upcoming appointment with <strong> %s</strong>.</p>
                        <div class="details">
                            <p><strong>Date:</strong> %s</p>
                            <p><strong>Time:</strong> %s</p>
                            <p><strong>Location:</strong> %s</p>
                        </div>
                        <p>We look forward to seeing you fit soon!</p>
                    </div>
                    <div class="footer">Best regards,<br>Omni Health App Team</div>
                </body>
                </html>
                """,
                appointment.getUsername(),
                appointment.getDoctorName(),
                appointment.getAppointmentDateTime().toLocalDate(),
                appointment.getAppointmentDateTime().toLocalTime(),
                appointment.getAppointmentPlace()
        );
    }

    private String buildGuardianEmailContent(UserAppointmentSchedule appointment, String guardianName) {
        return String.format(
                """
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                        }
                        .header {
                            background-color: #4CAF50;
                            color: white;
                            padding: 10px 20px;
                            text-align: center;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                        }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            font-size: 14px;
                            color: #777777;
                        }
                        .details {
                            margin: 10px 0;
                            padding: 10px;
                            background-color: #f9f9f9;
                            border: 1px solid #dddddd;
                            border-radius: 5px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">Appointment Reminder</div>
                    <div class="content">
                        <p>Dear <strong>%s</strong>,</p>
                        <p>The user <strong>%s</strong> has an upcoming appointment with <strong> %s</strong>.</p>
                        <div class="details">
                            <p><strong>Date:</strong> %s</p>
                            <p><strong>Time:</strong> %s</p>
                            <p><strong>Location:</strong> %s</p>
                        </div>
                        <p>We look forward to seeing your dependent fit soon!</p>
                    </div>
                    <div class="footer">Best regards,<br>Omni Health App Team</div>
                </body>
                </html>
                """,
                guardianName,
                appointment.getUsername(),
                appointment.getDoctorName(),
                appointment.getAppointmentDateTime().toLocalDate(),
                appointment.getAppointmentDateTime().toLocalTime(),
                appointment.getAppointmentPlace()
        );
    }

}
