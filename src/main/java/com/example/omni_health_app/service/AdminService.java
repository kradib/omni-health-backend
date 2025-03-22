package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.domain.repositories.UserDetailsRepository;
import com.example.omni_health_app.dto.request.AddUserRequest;
import com.example.omni_health_app.dto.request.AdminSignInRequest;
import com.example.omni_health_app.dto.request.UserDetailsUpdateRequest;
import com.example.omni_health_app.dto.response.UserDetailWithRole;
import com.example.omni_health_app.dto.response.UserDetailsWithRoleResponseData;
import com.example.omni_health_app.dto.response.UserSignInResponseData;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.omni_health_app.util.HashUtil.isHashMatch;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {


    private final UserAuthRepository userAuthRepository;
    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final UserAuthService userAuthService;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;

    @Value("${omni.auth.masterKey}")
    private String encryptedMasterKey;
    @Value("${omni.auth.adminKey}")
    private String encryptedAdminKey;


    public UserDetail addUser(AddUserRequest request) throws UserAuthException, BadRequestException {
        log.info("request {}", request);
        if ("doctor".equalsIgnoreCase(request.getRoles())) {
            if (request.getMajor() == null || request.getMajor().trim().isEmpty()) {
                throw new BadRequestException("Major is required for Doctor role.");
            }
            if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
                throw new BadRequestException("Location is required for Doctor role.");
            }
        }
        if("admin".equalsIgnoreCase(request.getRoles())) {
            if (!isHashMatch((request).getAdminMasterKey(), encryptedMasterKey)) {
                throw new BadRequestException("Admin details can't be added");
            }
        }
        if (userAuthRepository.existsByUsername(request.getUsername())) {
            throw new UserAuthException("Username is already taken");
        }
        if (userDetailsRepository.existsByEmail(request.getEmail())) {
            throw new UserAuthException("Email Id belongs to some one else");
        }
        if (userDetailsRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAuthException("Phone Number belongs to some one else");
        }
        final UserDetail userDetail = UserDetail.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .major(request.getMajor())
                .location(request.getLocation())
                .bloodGroup(request.getBloodGroup())
                .height(request.getHeight())
                .weight(request.getWeight())
                .gender(request.getGender())
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

    public UserSignInResponseData signIn(AdminSignInRequest request) throws UserAuthException {
        UserAuth userAuth = userAuthRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserAuthException("The User name does not exist"));

        if (!passwordEncoder.matches(request.getPassword(), userAuth.getPassword())) {
            throw new UserAuthException("Invalid credential provided");
        }

        if (!isHashMatch(request.getMasterKey(), encryptedMasterKey)) {
            throw new UserAuthException("Invalid credential provided");
        }
        if(!"admin".equalsIgnoreCase(userAuth.getRoles())) {
            throw new UserAuthException("Invalid attempt to log in");
        }

        return UserSignInResponseData.builder()
                .authToken(tokenUtil.generateToken(userAuth.getUsername(),userAuth))
                .userDetail(userAuth.getUserDetail())
                .build();
    }



    public UserDetail updateUser(Long userId, UserDetailsUpdateRequest request) throws BadRequestException, UserAuthException {
        UserDetail existingUser = userDetailsRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return userAuthService.updateUserDetails(existingUser.getUserAuth().getUsername(), request);
    }

    public void deleteUser(Long userId) throws UserAuthException {
        UserAuth existingUser = userAuthRepository.findById(userId)
                .orElseThrow(() -> new UserAuthException("User not found"));

        userAuthRepository.delete(existingUser);
        log.info("Successfully deleted user with ID {}", userId);
    }

    public UserDetailsWithRoleResponseData listUsers(String roles, final int page, final int size, final String name) {
        Pageable pageable = PageRequest.of(page, size);
        final Page<UserAuth> users = userAuthRepository.findByRoleAndName(roles == null || roles.isEmpty() ? null : roles,
                name == null || name.isEmpty() ? null : name,
                pageable);

        final List<UserDetailWithRole> userDetailWithRoles = users.stream()
                .map(userAuth -> new UserDetailWithRole(userAuth.getUserDetail(), userAuth.getRoles()))
                .toList();
        return UserDetailsWithRoleResponseData.builder()
                .userDetailWithRole(userDetailWithRoles)
                .success(true)
                .currentPage(page)
                .totalPages(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .build();
    }

}
