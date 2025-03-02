package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query("UPDATE UserAppointmentSchedule u SET u.appointmentStatus = :status WHERE u.id = :id")
    int cancelAppointmentById(Long id, String status);

    @Query("SELECT u FROM UserAppointmentSchedule u " +
            "JOIN u.userDetail d " +
            "WHERE (u.username = :username " +
            "       OR d.firstGuardianUserId = :username " +
            "       OR d.secondGuardianUserId = :username) " +
            "AND u.appointmentDateTime BETWEEN :startDate AND :endDate")
    Page<UserAppointmentSchedule> findAppointmentsByUserAndDateRange(
            @Param("username") String username,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );


    @Query("SELECT u FROM UserAppointmentSchedule u WHERE u.appointmentStatus != \"cancelled\" AND u.appointmentDateTime BETWEEN :startDateTime" +
            " AND :endDateTime")
    List<UserAppointmentSchedule> findPendingAppointments(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT u FROM UserAppointmentSchedule u " +
    "JOIN u.userDetail d " +
    "WHERE (:startDate IS NULL OR u.appointmentDateTime >= :startDate) " +
    "AND (:endDate IS NULL OR u.appointmentDateTime <= :endDate) " +
    "AND (d.id = :doctorId)"
     )
     Page<UserAppointmentSchedule> findAppointments(
     @Param("startDate") LocalDateTime startDate,
     @Param("endDate") LocalDateTime endDate,
     @Param("doctorId") Long doctorId,
     Pageable pageable
     );




}
