package com.example.omni_health_app.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAppointmentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(nullable = false, length = 500)
    private String appointmentPlace;

    @Column(nullable = false, length = 100)
    private String doctorName;

    @Column(unique = true)
    private Long userDetailsId;

    @Column
    private Integer status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_details_id", referencedColumnName = "id")
    private UserDetails userDetails;
}
