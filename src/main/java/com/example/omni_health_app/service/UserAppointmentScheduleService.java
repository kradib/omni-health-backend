package com.example.omni_health_app.service;

import com.example.omni_health_app.calculator.AppointmentSlotCountCalculator;
import com.example.omni_health_app.domain.entity.AppointmentSlot;
import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.model.AppointmentSlotCounts;
import com.example.omni_health_app.domain.model.AppointmentStatus;
import com.example.omni_health_app.domain.repositories.AppointmentSlotRepository;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.domain.repositories.UserDetailsRepository;
import com.example.omni_health_app.dto.model.AppointmentSlotAvailable;
import com.example.omni_health_app.dto.request.CancelAppointmentRequest;
import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.request.UpdateAppointmentRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAppointmentScheduleService {

    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final AppointmentSlotCountCalculator appointmentSlotCountCalculator;

    public List<AppointmentSlotAvailable> getAppointmentSlotsPerDoctor(final long doctorId,
                                                                            final LocalDate date) throws BadRequestException {
        final Optional<UserDetail> doctorDetails = userDetailsRepository.findById(doctorId);
        if(doctorDetails.isEmpty()) {
            throw new BadRequestException(String.format("doctor with id %s does not exists", doctorId));
        }
        return appointmentSlotCountCalculator.calculateAvailableSlot(
                appointmentSlotRepository.getAppointmentSlotCountsByDocAndDate(doctorId, date));
    }


    public CreateAppointmentResponseData createAppointmentSchedule( final String userName, CreateAppointmentRequest dto) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        final Optional<UserDetail> doctorDetails = userDetailsRepository.findById(dto.getDoctorId());
        if(doctorDetails.isEmpty()) {
            throw new BadRequestException(String.format("doctor with id %s does not exists", dto.getDoctorId()));
        }
        return userAuthOptional.map(userAuth -> {
            final UserAppointmentSchedule schedule = UserAppointmentSchedule.builder()
                    .appointmentDateTime(dto.getAppointmentDateTime())
                    .doctorDetail(doctorDetails.get())
                    .username(userName)
                    .appointmentStatus(AppointmentStatus.CREATED.getStatus())
                    .userDetail(userAuth.getUserDetail())
                    .build();
            final UserAppointmentSchedule createdUserAppointmentSchedule =
                    userAppointmentScheduleRepository.save(schedule);
            return CreateAppointmentResponseData.builder()
                    .success(true)
                    .appointmentTime(createdUserAppointmentSchedule.getAppointmentDateTime())
                    .userName(createdUserAppointmentSchedule.getUsername())
                    .build();
        }).orElseThrow(()-> new BadRequestException(String.format("user %s does not exists",userName)));

    }

    public CancelAppointmentResponseData cancelAppointmentSchedule( final String userName, CancelAppointmentRequest dto) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if(userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("user %s does not exists", userName));
        }
        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional = userAppointmentScheduleRepository.findById(dto.getAppointmentId());
        if(userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException(String.format("AppointId %s for user %s does not exists", dto.getAppointmentId(), userName));
        }
        final UserAppointmentSchedule userAppointmentSchedule = userAppointmentScheduleOptional.get();
        if(!userAppointmentSchedule.getUsername().equals(userName)) {
            throw new BadRequestException(String.format("AppointId %s does not belong to this user %s ", dto.getAppointmentId(), userName));
        }
        userAppointmentSchedule.setAppointmentStatus(AppointmentStatus.CANCELLED.getStatus());
        userAppointmentScheduleRepository.save(userAppointmentSchedule);
        return CancelAppointmentResponseData.builder()
                .success(true)
                .appointmentId(dto.getAppointmentId())
                .build();
    }

    public UpdateAppointmentResponseData updateAppointmentSchedule(final String userName, Long appointmentId,
                                                                   UpdateAppointmentRequest dto) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if(userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("user %s does not exists", userName));
        }
        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional = userAppointmentScheduleRepository.findById(appointmentId);
        if(userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException(String.format("AppointId %s for user %s does not exists", appointmentId, userName));
        }
        final UserAppointmentSchedule userAppointmentSchedule = userAppointmentScheduleOptional.get();
        if(!userAppointmentSchedule.getUsername().equals(userName)) {
            throw new BadRequestException(String.format("AppointId %s does not belong to this user %s ",appointmentId, userName));
        }
        userAppointmentSchedule.setAppointmentStatus(AppointmentStatus.UPDATED.getStatus());
        userAppointmentSchedule.setAppointmentDateTime(dto.getAppointmentDateTime());
        final UserAppointmentSchedule updatedUserAppointmentSchedule = userAppointmentScheduleRepository.save(userAppointmentSchedule);
        return UpdateAppointmentResponseData.builder()
                .success(true)
                .userAppointmentSchedule(updatedUserAppointmentSchedule)
                .build();
    }


    public GetAllAppointmentResponseData getAllAppointmentSchedule(
            final String userName,
            final LocalDateTime startDate,
            final LocalDateTime endDate,
            final String status,
            final Pageable pageable) throws BadRequestException {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize;
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("User %s does not exist", userName));
        }
        log.info("offset: {}, pageSize: {}", offset, pageSize);
        final List<UserAppointmentSchedule> appointments =
                userAppointmentScheduleRepository.findAppointmentsByUserAndDateRange(userName, startDate, endDate,
                        status, pageSize, offset);
        log.info("ownAppointments: {}", appointments);
        long totalRecords = userAppointmentScheduleRepository.countAppointmentsByUserAndDateRange(userName, startDate
                , endDate, status);
        Page<UserAppointmentSchedule> userAppointmentSchedulesPage = new PageImpl<>(appointments, pageable, totalRecords);
        return GetAllAppointmentResponseData.builder()
                .success(true)
                .appointments(appointments)
                .totalPages(userAppointmentSchedulesPage.getTotalPages())
                .totalElements(userAppointmentSchedulesPage.getTotalElements())
                .currentPage(userAppointmentSchedulesPage.getNumber())
                .build();
    }

    public GetAllAppointmentResponseData getAllAppointmentScheduleForDependents(
            final String userName,
            final LocalDateTime startDate,
            final LocalDateTime endDate,
            final String status,
            final Pageable pageable) throws BadRequestException {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize;
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("User %s does not exist", userName));
        }
        final List<UserAppointmentSchedule> appointments =
                userAppointmentScheduleRepository.findAppointmentsByDependentAndDateRange(userName, startDate, endDate,
                        status, pageSize, offset);
        log.info("dependent Appointments: {}", appointments);
        long totalRecords = userAppointmentScheduleRepository.countAppointmentsByDependentAndDateRange(userName,
                startDate, endDate, status);
        Page<UserAppointmentSchedule> userAppointmentSchedulesPage = new PageImpl<>(appointments, pageable, totalRecords);
        return GetAllAppointmentResponseData.builder()
                .success(true)
                .appointments(appointments)
                .totalPages(userAppointmentSchedulesPage.getTotalPages())
                .totalElements(userAppointmentSchedulesPage.getTotalElements())
                .currentPage(userAppointmentSchedulesPage.getNumber())
                .build();
    }

    public GetAppointmentResponseData getAppointmentSchedule(String userName, Long appointmentId) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if(userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("user %s does not exists", userName));
        }

        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional =
                userAppointmentScheduleRepository.findById(appointmentId);
        if(userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException(String.format("Appointment for user %s does not exists", userName));
        }
        return GetAppointmentResponseData.builder()
                .success(true)
                .appointmentSchedule(userAppointmentScheduleOptional.get())
                .build();
    }

}
