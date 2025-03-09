package com.example.omni_health_app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String roles;
    private String password;
}
