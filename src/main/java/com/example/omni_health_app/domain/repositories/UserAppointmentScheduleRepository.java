package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserAppointmentScheduleRepository extends JpaRepository<UserAppointmentSchedule, Long> {

    @Modifying
    @Query("UPDATE UserAppointmentSchedule u SET u.status = :status WHERE u.id = :id")
    int cancelAppointmentById(Long id, Integer status);

    @Query("SELECT u FROM UserAppointmentSchedule u " +
            "JOIN u.userDetail d " +
            "WHERE (u.username = :username " +
            "       OR d.firstGuardianUserId = :username " +
            "       OR d.secondGuardianUserId = :username) " +
            "AND u.appointmentDateTime BETWEEN :startDate AND :endDate")
    List<UserAppointmentSchedule> findAppointmentsByUserAndDateRange(
            @Param("username") String username,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
