package com.example.omni_health_app.component;

import com.example.omni_health_app.service.AppointmentReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentReminderComponent {


    private final AppointmentReminderService appointmentReminderService;


    @Scheduled(cron = "0 0 9 * * ?") //will run every day at 9:00 UTC
    public void sendAppointmentNotifications() {
        appointmentReminderService.notifyPendingAppointments();
    }

}
