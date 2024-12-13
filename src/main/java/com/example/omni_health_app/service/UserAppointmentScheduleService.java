package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.response.CreateAppointmentResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAppointmentScheduleService {

    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;


    public CreateAppointmentResponseData createAppointmentSchedule(CreateAppointmentRequest dto) {
        final UserAppointmentSchedule schedule = UserAppointmentSchedule.builder()
                .appointmentPlace(dto.getAppointmentPlace())
                .appointmentDateTime(dto.getAppointmentDateTime())
                .doctorName(dto.getDoctorName())
                .username(dto.getUsername())
                .userDetailsId(dto.getUserDetailsId())
                .build();

        final UserAppointmentSchedule createdUserAppointmentSchedule =
                userAppointmentScheduleRepository.save(schedule);
        return CreateAppointmentResponseData.builder()
                .success(true)
                .appointmentTime(createdUserAppointmentSchedule.getAppointmentDateTime())
                .userName(createdUserAppointmentSchedule.getUsername())
                .build();
    }

}
