package com.example.omni_health_app.dto.model;

import com.example.omni_health_app.domain.model.Slot;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppointmentSlotAvailable {

    private final Slot slot;
    private final int availableSLots;

}
