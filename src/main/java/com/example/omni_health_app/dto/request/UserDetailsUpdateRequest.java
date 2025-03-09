package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String firstGuardianUserId;
    private String secondGuardianUserId;

}
