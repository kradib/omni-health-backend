package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateAppointmentRequest {
    private LocalDateTime appointmentDateTime;
    private String appointmentPlace;
    private String doctorName;
}
