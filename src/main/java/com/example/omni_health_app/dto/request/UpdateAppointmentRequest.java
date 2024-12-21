package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UpdateAppointmentRequest {

    private String username;
    private LocalDateTime appointmentDateTime;
    private String appointmentPlace;
    private String doctorName;
}
