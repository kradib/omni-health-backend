package com.example.omni_health_app.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    @OneToOne(mappedBy = "userDetails")
    private UserAuth userAuth;
}
