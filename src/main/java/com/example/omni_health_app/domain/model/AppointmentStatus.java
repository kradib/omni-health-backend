package com.example.omni_health_app.domain.model;

import lombok.Getter;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum AppointmentStatus {
    CREATED("created"),
    CANCELLED("cancelled"),
    UPDATED("updated"),
    CONFIRMED("confirmed");

    private final String status;

    private static final Map<String, AppointmentStatus> byStatus = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(appointmentStatus -> appointmentStatus.status,
                    appointmentStatus -> appointmentStatus));

    AppointmentStatus(String i) {
        this.status = i;
    }

    public static AppointmentStatus from(int fromValue) {
        return byStatus.get(fromValue);
    }
}
