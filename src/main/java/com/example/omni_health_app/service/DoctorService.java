package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.model.AppointmentStatus;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.request.UpdateAppointmentStatusRequest;
import com.example.omni_health_app.dto.response.ListDoctorsResponseData;
import com.example.omni_health_app.dto.response.UpdateAppointmentResponseData;
import com.example.omni_health_app.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.omni_health_app.util.Constants.DOCTOR_ROLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {
    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final UserAuthRepository userAuthRepository;

     public UpdateAppointmentResponseData updateAppointmentScheduleStatus(final String userName, Long appointmentId,
                                                                   UpdateAppointmentStatusRequest dto) throws BadRequestException {
       

        if (dto.getPrescription() == null || dto.getPrescription().trim().isEmpty()) {
            throw new BadRequestException("Prescription required when status is completed");
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

        userAppointmentSchedule.setAppointmentStatus(AppointmentStatus.COMPLETED.getStatus());
        userAppointmentSchedule.setPrescription(dto.getPrescription());

        final UserAppointmentSchedule updatedUserAppointmentSchedule = userAppointmentScheduleRepository.save(userAppointmentSchedule);
        return UpdateAppointmentResponseData.builder()
                .success(true)
                .userAppointmentSchedule(updatedUserAppointmentSchedule)
                .build();
    }

    public ListDoctorsResponseData listDoctors(int page, String name, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserAuth> users = userAuthRepository.findByRoleAndName(DOCTOR_ROLE, name, pageable);
        final List<UserDetail> userDetails =  users.stream()
                .map(UserAuth::getUserDetail)
                .toList();
        return ListDoctorsResponseData.builder()
                .doctorDetails(userDetails)
                .success(true)
                .totalPages(users.getTotalPages())
                .currentPage(page)
                .totalElements(users.getTotalElements())
                .build();


    }


}
