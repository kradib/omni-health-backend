package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.AppointmentSlot;
import com.example.omni_health_app.domain.model.AppointmentSlotCounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    @Query("SELECT new com.example.omni_health_app.domain.model.AppointmentSlotCounts(a.slotId, COALESCE(SUM(a.numberOfAppointments), 0)) " +
            "FROM AppointmentSlot a " +
            "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate " +
            "GROUP BY a.slotId")
    List<AppointmentSlotCounts> getAppointmentSlotCountsByDocAndDate(
            @Param("doctorId") Long doctorId,
            @Param("appointmentDate") LocalDate appointmentDate
    );
}
