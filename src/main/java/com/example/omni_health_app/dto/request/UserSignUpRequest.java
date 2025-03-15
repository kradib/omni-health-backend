package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class UserSignUpRequest {

    @NonNull
    private String username;
    @NonNull
    private String password;
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String email;
    private String phoneNumber;
    private String firstGuardianUserId;
    private String secondGuardianUserId;
    @NonNull
    private LocalDate dateOfBirth;
    private Double weight;
    private Integer height;
    private String bloodGroup;

}
