package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.model.AppointmentStatus;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.request.CancelAppointmentRequest;
import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.request.UpdateAppointmentRequest;
import com.example.omni_health_app.dto.response.CancelAppointmentResponseData;
import com.example.omni_health_app.dto.response.CreateAppointmentResponseData;
import com.example.omni_health_app.dto.response.UpdateAppointmentResponseData;
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

    public CancelAppointmentResponseData cancelAppointmentSchedule(CancelAppointmentRequest dto) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(dto.getUsername());
        if(userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("user %s does not exists", dto.getUsername()));
        }
        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional = userAppointmentScheduleRepository.findById(dto.getAppointmentId());
        if(userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException(String.format("AppointId %s for user %s does not exists", dto.getAppointmentId(), dto.getUsername()));
        }
        final UserAppointmentSchedule userAppointmentSchedule = userAppointmentScheduleOptional.get();
        if(!userAppointmentSchedule.getUsername().equals(dto.getUsername())) {
            throw new BadRequestException(String.format("AppointId %s does not belong to this user %s ", dto.getAppointmentId(), dto.getUsername()));
        }
        userAppointmentSchedule.setStatus(AppointmentStatus.CANCELLED.getStatus());
        userAppointmentScheduleRepository.save(userAppointmentSchedule);
        return CancelAppointmentResponseData.builder()
                .success(true)
                .appointmentId(dto.getAppointmentId())
                .build();
    }

    public UpdateAppointmentResponseData updateAppointmentSchedule(
            Long appointmentId, UpdateAppointmentRequest dto) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(dto.getUsername());
        if(userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("user %s does not exists", dto.getUsername()));
        }
        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional = userAppointmentScheduleRepository.findById(appointmentId);
        if(userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException(String.format("AppointId %s for user %s does not exists", appointmentId, dto.getUsername()));
        }
        final UserAppointmentSchedule userAppointmentSchedule = userAppointmentScheduleOptional.get();
        if(!userAppointmentSchedule.getUsername().equals(dto.getUsername())) {
            throw new BadRequestException(String.format("AppointId %s does not belong to this user %s ",appointmentId, dto.getUsername()));
        }
        userAppointmentSchedule.setStatus(AppointmentStatus.UPDATED.getStatus());
        userAppointmentSchedule.setAppointmentPlace(dto.getAppointmentPlace());
        userAppointmentSchedule.setAppointmentDateTime(dto.getAppointmentDateTime());
        userAppointmentSchedule.setDoctorName(dto.getDoctorName());
        final UserAppointmentSchedule updatedUserAppointmentSchedule = userAppointmentScheduleRepository.save(userAppointmentSchedule);
        return UpdateAppointmentResponseData.builder()
                .success(true)
                .appointmentTime(updatedUserAppointmentSchedule.getAppointmentDateTime())
                .userName(updatedUserAppointmentSchedule.getUsername())
                .doctorName(updatedUserAppointmentSchedule.getDoctorName())
                .build();
    }

}
