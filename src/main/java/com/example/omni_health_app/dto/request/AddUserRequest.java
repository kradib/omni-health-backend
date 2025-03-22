package com.example.omni_health_app.dto.request;

import com.example.omni_health_app.domain.model.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private String email;
    @NonNull
    @Unique
    private String phoneNumber;
    @NonNull
    private String roles;

    private String major;
    private String location;
    @NonNull
    private LocalDate dateOfBirth;
    private Double weight;
    private Integer height;
    private String bloodGroup;
    private Gender gender;
    private String adminMasterKey;

}
