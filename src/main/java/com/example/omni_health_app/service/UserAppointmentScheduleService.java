package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.model.AppointmentStatus;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.response.CreateAppointmentResponseData;
import com.example.omni_health_app.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAppointmentScheduleService {

    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final UserAuthRepository userAuthRepository;


    public CreateAppointmentResponseData createAppointmentSchedule(CreateAppointmentRequest dto) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(dto.getUsername());
        return userAuthOptional.map(userAuth -> {
            final UserAppointmentSchedule schedule = UserAppointmentSchedule.builder()
                    .appointmentPlace(dto.getAppointmentPlace())
                    .appointmentDateTime(dto.getAppointmentDateTime())
                    .doctorName(dto.getDoctorName())
                    .username(dto.getUsername())
                    .status(AppointmentStatus.CREATED.getStatus())
                    .userDetail(userAuth.getUserDetail())
                    .build();
            final UserAppointmentSchedule createdUserAppointmentSchedule =
                    userAppointmentScheduleRepository.save(schedule);
            return CreateAppointmentResponseData.builder()
                    .success(true)
                    .appointmentTime(createdUserAppointmentSchedule.getAppointmentDateTime())
                    .userName(createdUserAppointmentSchedule.getUsername())
                    .build();
        }).orElseThrow(()-> new BadRequestException(String.format("user %s does not exists", dto.getUsername())));

    }

}
