package com.example.omni_health_app.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Slot {

    private final int id;
    private final String time;

}
