package com.example.omni_health_app.service;

import com.example.omni_health_app.calculator.AppointmentSlotCountCalculator;
import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.model.AppointmentStatus;
import com.example.omni_health_app.domain.model.UserRole;
import com.example.omni_health_app.domain.repositories.AppointmentSlotRepository;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.domain.repositories.UserDetailsRepository;
import com.example.omni_health_app.dto.model.AppointmentSlotAvailable;
import com.example.omni_health_app.dto.request.CancelAppointmentRequest;
import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.request.UpdateAppointmentRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.AppointmentAlreadyExistsException;
import com.example.omni_health_app.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.omni_health_app.domain.model.UserRole.*;
import static com.example.omni_health_app.util.EmailContentBuilder.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAppointmentScheduleService {

    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final AppointmentSlotCountCalculator appointmentSlotCountCalculator;
    private final INotificationService notificationService;

    public List<AppointmentSlotAvailable> getAppointmentSlotsPerDoctor(final long doctorId,
                                                                       final LocalDate date) throws BadRequestException {
        final Optional<UserDetail> doctorDetails = userDetailsRepository.findById(doctorId);
        if (doctorDetails.isEmpty()) {
            throw new BadRequestException(String.format("doctor with id %s does not exists", doctorId));
        }
        return appointmentSlotCountCalculator.calculateAvailableSlot(
                appointmentSlotRepository.getAppointmentSlotCountsByDocAndDate(doctorId, date), date);
    }

    @Transactional
    public CreateAppointmentResponseData createAppointmentSchedule(final String userName,
                                                                   CreateAppointmentRequest dto) throws BadRequestException, AppointmentAlreadyExistsException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        final Optional<UserDetail> doctorDetails = userDetailsRepository.findById(dto.getDoctorId());
        if (doctorDetails.isEmpty()) {
            throw new BadRequestException(String.format("doctor with id %s does not exists", dto.getDoctorId()));
        }
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("User with user name %s does not exists", userName));
        }
        final UserAuth userAuth = userAuthOptional.get();
        long appointCount =
                userAppointmentScheduleRepository.countBySlotIdDateUsernameAndDoctor(dto.getSlotId(),
                        dto.getAppointmentDateTime().toLocalDate(),
                        userAuth.getUsername());
        if (appointCount > 0) {
            throw new AppointmentAlreadyExistsException("You have an already existing appointment with a doctor");
        }
        final UserAppointmentSchedule schedule = UserAppointmentSchedule.builder()
                .appointmentDateTime(dto.getAppointmentDateTime())
                .doctorDetail(doctorDetails.get())
                .username(userName)
                .appointmentStatus(AppointmentStatus.CREATED.getStatus())
                .userDetail(userAuth.getUserDetail())
                .slotId(dto.getSlotId())
                .build();
        final UserAppointmentSchedule createdUserAppointmentSchedule =
                userAppointmentScheduleRepository.save(schedule);

        appointmentSlotRepository.upsertAppointmentSlot(createdUserAppointmentSchedule.getDoctorDetail().getId(),
                createdUserAppointmentSchedule.getSlotId(),
                createdUserAppointmentSchedule.getAppointmentDateTime().toLocalDate(), 1);

        notificationService.sendNotification(createdUserAppointmentSchedule.getUserDetail().getEmail(), "Appointment " +
                        "Created", buildUserEmailContentForAppointmentCreation(createdUserAppointmentSchedule));
        notificationService.sendNotification(createdUserAppointmentSchedule.getUserDetail().getEmail(), "Appointment " +
                "Created", buildUserEmailContentForAppointmentCreation(createdUserAppointmentSchedule));
        notificationService.sendNotification(createdUserAppointmentSchedule.getDoctorDetail().getEmail(), "Appointment " +
                "Created", buildDoctorEmailContentForAppointmentCreation(createdUserAppointmentSchedule));

        return CreateAppointmentResponseData.builder()
                .success(true)
                .appointmentTime(createdUserAppointmentSchedule.getAppointmentDateTime())
                .userName(createdUserAppointmentSchedule.getUsername())
                .build();

    }

    @Transactional
    public CancelAppointmentResponseData cancelAppointmentSchedule(final String userName,
     final String userRole, CancelAppointmentRequest dto) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("user %s does not exists", userName));
        }
        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional = userAppointmentScheduleRepository.findById(dto.getAppointmentId());
        if (userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException(String.format("AppointmentId %s for user %s does not exists",
                    dto.getAppointmentId(), userName));
        }
        final UserAppointmentSchedule userAppointmentSchedule = userAppointmentScheduleOptional.get();
        if (ROLE_PATIENT.toString().equals(userRole) && !userAppointmentSchedule.getUsername().equals(userName)) {
            throw new BadRequestException(String.format("AppointmentId %s does not belong to this user %s ",
                    dto.getAppointmentId(), userName));
        }
        if (ROLE_DOCTOR.toString().equals(userRole) && !userAppointmentSchedule.getDoctorDetail().getUserAuth().getUsername().equals(userName)) {
            throw new BadRequestException(String.format("AppointmentId %s does not belong to this doctor %s ",
                    dto.getAppointmentId(), userName));
        }

        if(userAppointmentSchedule.getAppointmentStatus().equals(AppointmentStatus.COMPLETED.getStatus())) {
            throw new BadRequestException("Appointment already completed");
        }
        userAppointmentSchedule.setAppointmentStatus(AppointmentStatus.CANCELLED.getStatus());
        userAppointmentScheduleRepository.save(userAppointmentSchedule);
        appointmentSlotRepository.upsertAppointmentSlot(userAppointmentSchedule.getDoctorDetail().getId(),
                userAppointmentSchedule.getSlotId(),
                userAppointmentSchedule.getAppointmentDateTime().toLocalDate(), -1);
        notificationService.sendNotification(userAppointmentSchedule.getUserDetail().getEmail(), "Appointment " +
                "Cancelled", buildUserEmailContentForAppointmentCancellation(userAppointmentSchedule));
        notificationService.sendNotification(userAppointmentSchedule.getDoctorDetail().getEmail(), "Appointment " +
                "Cancelled", buildDoctorEmailContentForAppointmentCancellation(userAppointmentSchedule));
        return CancelAppointmentResponseData.builder()
                .success(true)
                .appointmentId(dto.getAppointmentId())
                .build();
    }

    @Transactional
    public UpdateAppointmentResponseData updateAppointmentSchedule(final String userName, final String userRole,
                                                                   Long appointmentId,
                                                                   UpdateAppointmentRequest dto) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("user %s does not exists", userName));
        }
        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional = userAppointmentScheduleRepository.findById(appointmentId);
        if (userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException(String.format("AppointId %s for user %s does not exists", appointmentId, userName));
        }
        final UserAppointmentSchedule userAppointmentSchedule = userAppointmentScheduleOptional.get();
        if (ROLE_PATIENT.toString().equals(userRole) && !userAppointmentSchedule.getUsername().equals(userName)) {
            throw new BadRequestException(String.format("AppointId %s does not belong to this user %s ", appointmentId, userName));
        }

        if (userAppointmentSchedule.getAppointmentStatus().equals(AppointmentStatus.CANCELLED.getStatus())) {
            throw new BadRequestException(String.format("Appointment Id %s already cancelled ", appointmentId));
        }
        if (userAppointmentSchedule.getAppointmentStatus().equals(AppointmentStatus.COMPLETED.getStatus())) {
            throw new BadRequestException(String.format("Appointment Id %s already completed ", appointmentId));
        }

        LocalDate previousAppointmentDate = userAppointmentSchedule.getAppointmentDateTime().toLocalDate();
        int previousAppointmentSlot = userAppointmentSchedule.getSlotId();


        userAppointmentSchedule.setAppointmentDateTime(dto.getAppointmentDateTime());
        userAppointmentSchedule.setSlotId(dto.getSlotId());

        final UserAppointmentSchedule updatedUserAppointmentSchedule = userAppointmentScheduleRepository.save(userAppointmentSchedule);
        appointmentSlotRepository.upsertAppointmentSlot(userAppointmentSchedule.getDoctorDetail().getId(),
                userAppointmentSchedule.getSlotId(),
                userAppointmentSchedule.getAppointmentDateTime().toLocalDate(), 1);
        appointmentSlotRepository.upsertAppointmentSlot(userAppointmentSchedule.getDoctorDetail().getId(),
                previousAppointmentSlot,
                previousAppointmentDate, -1);
        notificationService.sendNotification(userAppointmentSchedule.getUserDetail().getEmail(), "Appointment " +
                "Updated", buildUserEmailContentForAppointmentUpdate(userAppointmentSchedule));
        notificationService.sendNotification(userAppointmentSchedule.getDoctorDetail().getEmail(), "Appointment " +
                "Updated", buildDoctorEmailContentForAppointmentUpdate(userAppointmentSchedule));

        return UpdateAppointmentResponseData.builder()
                .success(true)
                .userAppointmentSchedule(updatedUserAppointmentSchedule)
                .build();
    }


    public GetAllAppointmentResponseData getAllAppointmentSchedule(
            final String userName,
            final UserRole userRole,
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
        return switch (userRole) {
            case ROLE_PATIENT -> getAllAppointmentScheduleForUsers(userName, startDate, endDate, status, pageSize,
                    offset, pageable);
            case ROLE_ADMIN -> getAllAppointmentSchedule(startDate, endDate, status, pageable);
            case ROLE_DOCTOR ->
                    getAllAppointmentScheduleForDoctor(userName, startDate, endDate, status, pageSize,
                            offset, pageable);
        };

    }

    private GetAllAppointmentResponseData getAllAppointmentScheduleForUsers(final String userName,
                                                                            final LocalDateTime startDate,
                                                                            final LocalDateTime endDate,
                                                                            final String status,
                                                                            final int pageSize,
                                                                            final int offset,
                                                                            final Pageable pageable) {
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

    public GetAllAppointmentResponseData getAllAppointmentSchedule(
            final LocalDateTime startDate,
            final LocalDateTime endDate,
            final String status,
            final Pageable pageable) {
        final Page<UserAppointmentSchedule> userAppointmentSchedulesPage =
                userAppointmentScheduleRepository.findAppointments(startDate, endDate, status, pageable);
        final List<UserAppointmentSchedule> appointments = userAppointmentSchedulesPage.getContent();
        final List<UserAppointmentSchedule> updatedAppointments = appointments.stream().map(appointmentSchedule ->
                UserAppointmentSchedule.builder()
                        .id(appointmentSchedule.getId())
                        .slotId(appointmentSchedule.getSlotId())
                        .appointmentStatus(appointmentSchedule.getAppointmentStatus())
                        .notes(null)
                        .userDetail(appointmentSchedule.getUserDetail())
                        .appointmentDateTime(appointmentSchedule.getAppointmentDateTime())
                        .doctorDetail(appointmentSchedule.getDoctorDetail())
                        .documents(null)
                        .prescription(null)
                        .username(appointmentSchedule.getUsername())
                        .build()
                ).toList();

        return GetAllAppointmentResponseData.builder()
                .success(true)
                .appointments(updatedAppointments)
                .totalPages(userAppointmentSchedulesPage.getTotalPages())
                .totalElements(userAppointmentSchedulesPage.getTotalElements())
                .currentPage(userAppointmentSchedulesPage.getNumber())
                .build();
    }

    private GetAllAppointmentResponseData getAllAppointmentScheduleForDoctor(final String userName,
                                                                            final LocalDateTime startDate,
                                                                            final LocalDateTime endDate,
                                                                            final String status,
                                                                            final int pageSize,
                                                                            final int offset,
                                                                            final Pageable pageable) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("Doctor %s does not exist", userName));
        }
        final List<UserAppointmentSchedule> appointments =
                userAppointmentScheduleRepository.findAppointmentsByDoctor(userAuthOptional.get().getId(), startDate, endDate,
                        status, pageSize, offset);
        log.info("Appointments for doctor {}: {}", userName, appointments);
        long totalRecords = userAppointmentScheduleRepository.countAppointmentsByDoctor(userAuthOptional.get().getId(), startDate
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

    public GetAppointmentResponseData getAppointmentSchedule(String userName, String userRole, Long appointmentId) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("user %s does not exists", userName));
        }

        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional =
                userAppointmentScheduleRepository.findById(appointmentId);
        if (userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException(String.format("Appointment for user %s does not exists", userName));
        }
        if(ROLE_PATIENT.toString().equals(userRole) && !userAppointmentScheduleOptional.get().getUsername().equals(userName)) {
            throw new BadRequestException(String.format("Appointment does not belong to  user %s ", userName));
        }
        if(ROLE_DOCTOR.toString().equals(userRole) && !userAppointmentScheduleOptional.get().getDoctorDetail().getUserAuth().getUsername().equals(userName)) {
            throw new BadRequestException(String.format("Appointment does not belong to  doctor %s ", userName));
        }

        final UserAppointmentSchedule userAppointmentSchedule = userAppointmentScheduleOptional.get();
        if(ROLE_ADMIN.toString().equals(userRole)) {
            userAppointmentSchedule.setPrescription(null);
            userAppointmentSchedule.setDocuments(null);
            userAppointmentSchedule.setNotes(null);
        }
        return GetAppointmentResponseData.builder()
                .success(true)
                .appointmentSchedule(userAppointmentSchedule)
                .build();
    }

}
