package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.model.AppointmentStatus;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.request.CancelAppointmentRequest;
import com.example.omni_health_app.dto.request.CreateAppointmentRequest;
import com.example.omni_health_app.dto.request.UpdateAppointmentRequest;
import com.example.omni_health_app.dto.response.CancelAppointmentResponseData;
import com.example.omni_health_app.dto.response.CreateAppointmentResponseData;
import com.example.omni_health_app.dto.response.GetAllAppointmentResponseData;
import com.example.omni_health_app.dto.response.GetAppointmentResponseData;
import com.example.omni_health_app.dto.response.UpdateAppointmentResponseData;
import com.example.omni_health_app.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAppointmentScheduleService {

    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final UserAuthRepository userAuthRepository;


    public CreateAppointmentResponseData createAppointmentSchedule( final String userName, CreateAppointmentRequest dto) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        return userAuthOptional.map(userAuth -> {
            final UserAppointmentSchedule schedule = UserAppointmentSchedule.builder()
                    .appointmentPlace(dto.getAppointmentPlace())
                    .appointmentDateTime(dto.getAppointmentDateTime())
                    .doctorName(dto.getDoctorName())
                    .username(userName)
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
        userAppointmentSchedule.setStatus(AppointmentStatus.CANCELLED.getStatus());
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


    public GetAllAppointmentResponseData getAllAppointmentSchedule(final String userName, final LocalDateTime startDate, final LocalDateTime endDate) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if(userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("user %s does not exists", userName));
        }
        final List<UserAppointmentSchedule> userAppointmentSchedules =
                userAppointmentScheduleRepository.findAppointmentsByUserAndDateRange(userName, startDate, endDate);
        if(userAppointmentSchedules.isEmpty()) {
            throw new BadRequestException(String.format("Appointment for user %s does not exists", userName));
        }

        final List<UserAppointmentSchedule> ownAppointments = userAppointmentSchedules.stream()
                .filter(appointment -> appointment.getUsername().equals(userName))
                .toList();

        Map<String, List<UserAppointmentSchedule>> dependentAppointments = userAppointmentSchedules.stream()
                .filter(appointment -> {
                    UserDetail userDetail = appointment.getUserDetail();
                    return userName.equals(userDetail.getFirstGuardianUserId()) ||  userName.equals(userDetail.getSecondGuardianUserId());
                })
                .collect(Collectors.groupingBy(appointment -> {
                    UserDetail userDetail = appointment.getUserDetail();
                    return  userDetail.getFirstName() + " " + userDetail.getLastName();
                }));

        return GetAllAppointmentResponseData.builder()
                .success(true)
                .ownAppointments(ownAppointments)
                .dependentAppointments(dependentAppointments)
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
