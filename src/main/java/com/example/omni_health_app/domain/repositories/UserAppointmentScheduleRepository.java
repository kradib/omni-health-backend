package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserAppointmentScheduleRepository extends JpaRepository<UserAppointmentSchedule, Long> {

    @Modifying
    @Query("UPDATE UserAppointmentSchedule u SET u.appointmentStatus = :status WHERE u.id = :id")
    int cancelAppointmentById(Long id, String status);

    @Query(value = "SELECT u.* FROM user_appointment_schedule u " +
            "JOIN user_detail d ON u.user_detail_id = d.id " +
            "WHERE u.username = :username " +
            "AND (:startDate IS NULL OR u.appointment_date_time >= :startDate) " +
            "AND (:endDate IS NULL OR u.appointment_date_time <= :endDate) " +
            "AND (:status IS NULL OR u.appointment_status = :status) " +
            "ORDER BY u.appointment_date_time DESC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<UserAppointmentSchedule> findAppointmentsByUserAndDateRange(
            @Param("username") String username,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM user_appointment_schedule u " +
            "JOIN user_detail d ON u.user_detail_id = d.id " +
            "WHERE u.username = :username " +
            "AND (:startDate IS NULL OR u.appointment_date_time >= :startDate) " +
            "AND (:endDate IS NULL OR u.appointment_date_time <= :endDate) "+
            "AND (:status IS NULL OR u.appointment_status = :status) ",
            nativeQuery = true)
    long countAppointmentsByUserAndDateRange(
            @Param("username") String username,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status);

    @Query(value = "SELECT u.* FROM user_appointment_schedule u " +
            "JOIN user_detail d ON u.user_detail_id = d.id " +
            "WHERE (d.first_guardian_user_id = :username OR d.second_guardian_user_id = :username) " +
            "AND (:startDate IS NULL OR u.appointment_date_time >= :startDate) " +
            "AND (:endDate IS NULL OR u.appointment_date_time <= :endDate) " +
            "AND (:status IS NULL OR u.appointment_status = :status) " +
            "ORDER BY u.appointment_date_time DESC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<UserAppointmentSchedule> findAppointmentsByDependentAndDateRange(
            @Param("username") String username,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM user_appointment_schedule u " +
            "JOIN user_detail d ON u.user_detail_id = d.id " +
            "WHERE (d.first_guardian_user_id = :username OR d.second_guardian_user_id = :username)" +
            "AND (:startDate IS NULL OR u.appointment_date_time >= :startDate) " +
            "AND (:endDate IS NULL OR u.appointment_date_time <= :endDate) " +
            "AND (:status IS NULL OR u.appointment_status = :status) ",
            nativeQuery = true)
    long countAppointmentsByDependentAndDateRange(
            @Param("username") String username,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status);

    @Query(value = "SELECT u.* FROM user_appointment_schedule u " +
            "WHERE u.doctor_detail_id = :doctorId " +
            "AND (:startDate IS NULL OR u.appointment_date_time >= :startDate) " +
            "AND (:endDate IS NULL OR u.appointment_date_time <= :endDate) " +
            "AND (:status IS NULL OR u.appointment_status = :status) " +
            "ORDER BY u.appointment_date_time DESC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<UserAppointmentSchedule> findAppointmentsByDoctor(
            @Param("doctorId") long doctorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM user_appointment_schedule u " +
            "WHERE u.doctor_detail_id = :doctorId " +
            "AND (:startDate IS NULL OR u.appointment_date_time >= :startDate) " +
            "AND (:endDate IS NULL OR u.appointment_date_time <= :endDate) " +
            "AND (:status IS NULL OR u.appointment_status = :status)",
            nativeQuery = true)
    long countAppointmentsByDoctor(
            @Param("doctorId") long doctorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status);

    @Query(value = "SELECT u.* FROM user_appointment_schedule u " +
            "WHERE (:startDate IS NULL OR u.appointment_date_time >= :startDate) " +
            "AND (:endDate IS NULL OR u.appointment_date_time <= :endDate) " +
            "AND (:status IS NULL OR u.appointment_status = :status) " +
            "ORDER BY u.appointment_date_time DESC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<UserAppointmentSchedule> findAppointments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset);





    @Query("SELECT u FROM UserAppointmentSchedule u WHERE u.appointmentStatus = \"created\" AND u" +
            ".appointmentDateTime BETWEEN :startDateTime" +
            " AND :endDateTime")
    List<UserAppointmentSchedule> findPendingAppointments(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT u FROM UserAppointmentSchedule u " +
            "WHERE (:startDate IS NULL OR u.appointmentDateTime >= :startDate) " +
            "AND (:endDate IS NULL OR u.appointmentDateTime <= :endDate) " +
            "AND (:status IS NULL OR u.appointmentStatus = :status)")
    Page<UserAppointmentSchedule> findAppointments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            Pageable pageable);


    @Query("SELECT COUNT(u) FROM UserAppointmentSchedule u " +
            "WHERE u.slotId = :slotId " +
            "AND FUNCTION('DATE', u.appointmentDateTime) = :appointmentDate " +
            "AND u.username = :username " +
            "AND u.appointmentStatus != \"cancelled\"")
    long countBySlotIdDateUsernameAndDoctor(
            @Param("slotId") int slotId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("username") String username);





}
