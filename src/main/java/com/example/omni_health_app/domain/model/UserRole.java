package com.example.omni_health_app.domain.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum UserRole {

    ROLE_PATIENT("ROLE_PATIENT"), ROLE_DOCTOR("ROLE_DOCTOR"), ROLE_ADMIN("ROLE_ADMIN");

    private final String role;

    private static final Map<String, UserRole> byRole = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(userRole -> userRole.role,
                    userRole -> userRole));

    UserRole(String i) {
        this.role = i;
    }

    public static UserRole from(String fromValue) {
        return byRole.get(fromValue);
    }
}
