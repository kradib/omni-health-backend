package com.example.omni_health_app.dto.request;

import org.checkerframework.common.aliasing.qual.Unique;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AddUserRequest {

    @NonNull
    private String username;
    @NonNull
    private String password;
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    @Unique
    private String emailId;
    private String phoneNumber;
    @NonNull
    private String roles;

    private String major;
    private String location;

}
