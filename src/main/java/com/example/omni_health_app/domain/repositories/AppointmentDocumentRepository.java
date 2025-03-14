package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.AppointmentDocument;
import com.example.omni_health_app.domain.entity.DocumentEntity;
import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AppointmentDocumentRepository extends JpaRepository<AppointmentDocument, Long> {

    List<DocumentEntity> findByAppointment(UserAppointmentSchedule appointmentSchedule);


}
