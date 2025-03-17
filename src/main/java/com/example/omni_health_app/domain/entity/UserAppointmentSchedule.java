package com.example.omni_health_app.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(nullable = false)
    private int slotId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_detail_id", referencedColumnName = "id")
    private UserDetail userDetail;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_detail_id", referencedColumnName = "id")
    private UserDetail doctorDetail;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Notes> notes;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AppointmentDocument> documents;


    @Override
    public String toString() {
        return "UserAppointmentSchedule{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", appointmentDateTime=" + appointmentDateTime +
                ", appointmentStatus='" + appointmentStatus + '\'' +
                ", prescription='" + prescription + '\'' +
                ", slotId=" + slotId +
                ", userDetail=" + (userDetail != null ? userDetail.getId() : "null") +
                ", doctorDetail=" + (doctorDetail != null ? doctorDetail.getId() : "null") +
                '}';
    }
}
