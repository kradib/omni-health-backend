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

    @Column(nullable = false, length = 500)
    private String appointmentPlace;

    @Column(nullable = false, length = 100)
    private String doctorName;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private String appointmentStatus;

    @Column(nullable = true)
    private String prescription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_detail_id", referencedColumnName = "id")
    private UserDetail userDetail;
}
