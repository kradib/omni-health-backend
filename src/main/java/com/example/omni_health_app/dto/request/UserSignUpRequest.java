package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

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

}
