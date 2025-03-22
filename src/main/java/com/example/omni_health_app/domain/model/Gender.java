package com.example.omni_health_app.domain.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Gender {

    MALE("male"),
    FEMALE("female"),
    OTHERS("others");

    private final String gender;

    private static final Map<String, Gender> byStatus = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(gender -> gender.gender,
                    appointmentStatus -> appointmentStatus));

    Gender(String i) {
        this.gender = i;
    }

    public static Gender from(String fromValue) {
        return byStatus.get(fromValue);
    }
}
