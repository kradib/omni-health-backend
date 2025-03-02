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

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(nullable = false)
    private String appointmentStatus;

    @Column(nullable = true)
    private String prescription;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_detail_id", referencedColumnName = "id")
    private UserDetail userDetail;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_detail_id", referencedColumnName = "id")
    private UserDetail doctorDetail;
}
