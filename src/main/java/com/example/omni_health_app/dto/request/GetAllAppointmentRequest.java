package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class GetAllAppointmentRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
