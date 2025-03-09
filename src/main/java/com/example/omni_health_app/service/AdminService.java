package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.domain.repositories.UserDetailsRepository;
import com.example.omni_health_app.dto.request.UserSignInRequest;
import com.example.omni_health_app.dto.response.GetAllAppointmentResponseData;
import com.example.omni_health_app.dto.response.UserDetailWithRoles;
import com.example.omni_health_app.dto.request.AddUserRequest;
import com.example.omni_health_app.dto.request.UpdateUserRequest;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.omni_health_app.util.Constants.CACHE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {


    private final UserAuthRepository userAuthRepository;
    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final UserDetailsRepository userDetailsRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenUtil tokenUtil;
    private final CacheManager cacheManager;
    private final INotificationService notificationService;

    public UserDetail addUser(AddUserRequest request) throws UserAuthException {
        if ("doctor".equalsIgnoreCase(request.getRoles())) {
            if (request.getMajor() == null || request.getMajor().trim().isEmpty()) {
                throw new IllegalArgumentException("Major is required for Doctor role.");
            }
            if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
                throw new IllegalArgumentException("Location is required for Doctor role.");
            }
        }

        if (userAuthRepository.existsByUsername(request.getUsername())) {
            throw new UserAuthException("Username is already taken");
        }

        if (userDetailsRepository.existsByEmail(request.getEmailId())) {
            throw new UserAuthException("Email Id is already taken");
        }

        final UserDetail userDetail = UserDetail.builder()
                .email(request.getEmailId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        final UserAuth userAuth = UserAuth.builder()
                .userDetail(userDetail)
                .username(request.getUsername())
                .roles(request.getRoles())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        UserAuth savedUser = userAuthRepository.save(userAuth);
        log.info("Successfully Added user {}", savedUser.getUserDetail());
        return savedUser.getUserDetail();
    }

    public UserDetail updateUser(Long userId, UpdateUserRequest request) throws UserAuthException {
        UserAuth existingUser = userAuthRepository.findById(userId)
                .orElseThrow(() -> new UserAuthException("User not found"));

        // Update fields if present in request
        if (request.getEmail() != null) existingUser.getUserDetail().setEmail(request.getEmail());
        if (request.getFirstName() != null) existingUser.getUserDetail().setFirstName(request.getFirstName());
        if (request.getLastName() != null) existingUser.getUserDetail().setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) existingUser.getUserDetail().setPhoneNumber(request.getPhoneNumber());
        if (request.getRoles() != null) existingUser.setRoles(request.getRoles());
        if (request.getPassword() != null) existingUser.setPassword(passwordEncoder.encode(request.getPassword()));

        UserAuth updatedUser = userAuthRepository.save(existingUser);
        log.info("Successfully updated user {}", updatedUser.getUserDetail());
        return updatedUser.getUserDetail();
    }

    public void deleteUser(Long userId) throws UserAuthException {
        UserAuth existingUser = userAuthRepository.findById(userId)
                .orElseThrow(() -> new UserAuthException("User not found"));

        userAuthRepository.delete(existingUser);
        log.info("Successfully deleted user with ID {}", userId);
    }

    public List<UserDetailWithRoles> listUsers(String role) {
        List<UserAuth> users;

        if (role != null && !role.isEmpty()) {
            users = userAuthRepository.findByRolesContaining(role);
        } else {
            users = userAuthRepository.findAll();
        }

        return users.stream()
                .map(userAuth -> new UserDetailWithRoles(userAuth.getUserDetail(), userAuth.getRoles()))
                .collect(Collectors.toList());
    }

    public GetAllAppointmentResponseData getAllAppointmentSchedule(
            final Long doctorId,
            final LocalDateTime startDate,
            final LocalDateTime endDate,
            final Pageable pageable) {

        // Fetch appointments with optional date filters
        final Page<UserAppointmentSchedule> userAppointmentSchedulesPage =
                userAppointmentScheduleRepository.findAppointments(startDate, endDate, doctorId, pageable);

        // Convert to a list
        final List<UserAppointmentSchedule> appointments = userAppointmentSchedulesPage.getContent();

        return GetAllAppointmentResponseData.builder()
                .success(true)
                .appointments(appointments) // Include appointments in the response
                .totalPages(userAppointmentSchedulesPage.getTotalPages())
                .totalElements(userAppointmentSchedulesPage.getTotalElements())
                .currentPage(userAppointmentSchedulesPage.getNumber())
                .build();
    }

}
