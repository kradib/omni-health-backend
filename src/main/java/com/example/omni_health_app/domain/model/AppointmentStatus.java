package com.example.omni_health_app.domain.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum AppointmentStatus {
    CREATED(1),
    CANCELLED(2);

    private final int status;

    private static final Map<Integer, AppointmentStatus> byStatus = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(appointmentStatus -> appointmentStatus.status,
                    appointmentStatus -> appointmentStatus));

    AppointmentStatus(int i) {
        this.status = i;
    }
    public int getStatus() {
        return status;
    }
    public static AppointmentStatus from(int fromValue) {
        return byStatus.get(fromValue);
    }
}
