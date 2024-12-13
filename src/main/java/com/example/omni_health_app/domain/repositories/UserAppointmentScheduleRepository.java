package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAppointmentScheduleRepository extends JpaRepository<UserAppointmentSchedule, Long> {

}
