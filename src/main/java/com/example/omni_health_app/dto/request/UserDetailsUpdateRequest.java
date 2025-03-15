package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDetailsUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String firstGuardianUserId;
    private String secondGuardianUserId;
    private String major;
    private String location;
    private LocalDate dateOfBirth;
    private Double weight;
    private Integer height;
    private String bloodGroup;

}
