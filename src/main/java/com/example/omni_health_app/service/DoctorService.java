package com.example.omni_health_app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.model.AppointmentStatus;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.response.GetAllAppointmentResponseData;
import com.example.omni_health_app.dto.response.UserDetailWithRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.dto.request.UpdateAppointmentStatusRequest;
import com.example.omni_health_app.dto.response.UpdateAppointmentResponseData;
import com.example.omni_health_app.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

import static com.example.omni_health_app.util.Constants.DOCTOR_ROLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {
    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final UserAuthRepository userAuthRepository;

     public UpdateAppointmentResponseData updateAppointmentScheduleStatus(final String userName, Long appointmentId,
                                                                   UpdateAppointmentStatusRequest dto) throws BadRequestException {
       
        if (AppointmentStatus.COMPLETED.toString().equalsIgnoreCase(dto.getAppointmentStatus())) {
            if (dto.getPrescription() == null || dto.getPrescription().trim().isEmpty()) {
                throw new IllegalArgumentException("Prescription required when status is completed");
            }           
        }

        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional = userAppointmentScheduleRepository.findById(appointmentId);
        if(userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException(String.format("AppointId %s for user %s does not exists", appointmentId, userName));
        }
        final UserAppointmentSchedule userAppointmentSchedule = userAppointmentScheduleOptional.get();
        if(!userAppointmentSchedule.getDoctorDetail().getUserAuth().getUsername().equals(userName)) {
            throw new BadRequestException(String.format("AppointId %s should not be access by doctor " +
                    "%s ",appointmentId, userName));
        }

        userAppointmentSchedule.setAppointmentStatus(dto.getAppointmentStatus());
        userAppointmentSchedule.setPrescription(dto.getPrescription());

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
            throw new BadRequestException(String.format("Doctor %s does not exist", userName));
        }
        final List<UserAppointmentSchedule> appointments =
                userAppointmentScheduleRepository.findAppointmentsByDoctor(userAuthOptional.get().getUserDetail().getId(),
                        startDate, endDate,
                        status, pageSize, offset);
        log.info("patient Appointments: {}", appointments);
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

    public List<UserDetail> listDoctors() {
        List<UserAuth> users = userAuthRepository.findByRolesContaining(DOCTOR_ROLE);
        return users.stream()
                .map(UserAuth::getUserDetail)
                .collect(Collectors.toList());
    }


}
