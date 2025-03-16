package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAppointmentStatusRequest {
    private String prescription;
}
