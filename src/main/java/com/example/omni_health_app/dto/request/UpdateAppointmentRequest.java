package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UpdateAppointmentRequest {
    private LocalDateTime appointmentDateTime;
}
