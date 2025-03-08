package com.example.omni_health_app.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlotCounts {

    private int slotId;
    private long numberOfAppointments;
}
