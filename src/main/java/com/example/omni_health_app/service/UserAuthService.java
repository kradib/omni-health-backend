package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetails;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.request.UserSignUpRequest;
import com.example.omni_health_app.exception.UserAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService {


    private final UserAuthRepository userAuthRepository;

    private final PasswordEncoder passwordEncoder;

    public String signUp(UserSignUpRequest request) throws UserAuthException {
        if (userAuthRepository.existsByUsername(request.getUsername())) {
            throw new UserAuthException("Username is already taken");
        }

        boolean validFirstGuardianUserId = userAuthRepository.existsByUsername(request.getFirstGuardianUserId());
        boolean validSecondGuardianUserId = userAuthRepository.existsByUsername(request.getSecondGuardianUserId());
        log.info("the validity of userid {} is {}, userId {} is {}",
                request.getFirstGuardianUserId(), validFirstGuardianUserId, request.getSecondGuardianUserId(), validSecondGuardianUserId);

        final UserDetails userDetails = UserDetails.builder()
                .email(request.getEmailId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .firstGuardianUserId(validFirstGuardianUserId ? request.getFirstGuardianUserId(): null)
                .secondGuardianUserId(validSecondGuardianUserId? request.getSecondGuardianUserId(): null)
                .build();

        final UserAuth userAuth = UserAuth.builder()
                .userDetails(userDetails)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        UserAuth savedUser = userAuthRepository.save(userAuth);
        log.info("Successfully registered user {}", savedUser.getUserDetails());
        return userAuth.getUsername();
    }
}
