package com.example.omni_health_app.domain.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_documents")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    @JsonBackReference
    private UserAppointmentSchedule appointment;

    @Column(nullable = false)
    private String documentName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime dateUploaded;
}
