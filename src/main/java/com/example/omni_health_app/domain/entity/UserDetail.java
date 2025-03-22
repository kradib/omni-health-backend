package com.example.omni_health_app.domain.entity;

import com.example.omni_health_app.domain.model.Gender;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    private String firstGuardianUserId;
    private String secondGuardianUserId;
    private String major;
    private String location;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    private Double weight;
    private Integer height;
    private String bloodGroup;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @OneToOne(mappedBy = "userDetail")
    @JsonBackReference
    @ToString.Exclude
    private UserAuth userAuth;
}
