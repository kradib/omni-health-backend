package com.example.omni_health_app.domain.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_documents")
@Data
@Builder
public class AppointmentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private UserAppointmentSchedule appointment;

    @Column(nullable = false)
    private String documentName;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime dateUploaded;
}
