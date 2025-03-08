package com.example.omni_health_app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments_slot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_slot_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private UserDetail doctor;

    @Column(name = "slot_id", nullable = false)
    private int slotId;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "number_of_appointments", nullable = false)
    private int numberOfAppointments;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}