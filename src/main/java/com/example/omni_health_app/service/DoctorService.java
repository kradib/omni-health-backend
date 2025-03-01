package com.example.omni_health_app.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.dto.request.UpdateAppointmentStatusRequest;
import com.example.omni_health_app.dto.response.UpdateAppointmentResponseData;
import com.example.omni_health_app.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;

     public UpdateAppointmentResponseData updateAppointmentScheduleStatus(final String userName, Long appointmentId,
                                                                   UpdateAppointmentStatusRequest dto) throws BadRequestException {
       
        if ("completed".equalsIgnoreCase(dto.getAppointmentStatus())) {
            if (dto.getPrescription() == null || dto.getPrescription().trim().isEmpty()) {
                throw new IllegalArgumentException("Prescription required when status is completed");
            }           
        }

        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional = userAppointmentScheduleRepository.findById(appointmentId);
        if(userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException(String.format("AppointId %s for user %s does not exists", appointmentId, userName));
        }
        final UserAppointmentSchedule userAppointmentSchedule = userAppointmentScheduleOptional.get();
        if(!userAppointmentSchedule.getDoctorName().equals(userName)) {
            throw new BadRequestException(String.format("AppointId %s does not belong to this user %s ",appointmentId, userName));
        }

        userAppointmentSchedule.setAppointmentStatus(dto.getAppointmentStatus());
        userAppointmentSchedule.setPrescription(dto.getPrescription());

        final UserAppointmentSchedule updatedUserAppointmentSchedule = userAppointmentScheduleRepository.save(userAppointmentSchedule);
        return UpdateAppointmentResponseData.builder()
                .success(true)
                .appointmentTime(updatedUserAppointmentSchedule.getAppointmentDateTime())
                .userName(updatedUserAppointmentSchedule.getUsername())
                .doctorName(updatedUserAppointmentSchedule.getDoctorName())
                .build();
    }


}
