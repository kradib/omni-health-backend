package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.request.UserSignInRequest;
import com.example.omni_health_app.dto.request.UserSignUpRequest;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.util.TokenUtil;
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

    private final TokenUtil tokenUtil;

    public String signUp(UserSignUpRequest request) throws UserAuthException {
        if (userAuthRepository.existsByUsername(request.getUsername())) {
            throw new UserAuthException("Username is already taken");
        }

        boolean validFirstGuardianUserId = userAuthRepository.existsByUsername(request.getFirstGuardianUserId());
        boolean validSecondGuardianUserId = userAuthRepository.existsByUsername(request.getSecondGuardianUserId());
        log.info("the validity of userid {} is {}, userId {} is {}",
                request.getFirstGuardianUserId(), validFirstGuardianUserId, request.getSecondGuardianUserId(), validSecondGuardianUserId);

        final UserDetail userDetail = UserDetail.builder()
                .email(request.getEmailId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .firstGuardianUserId(validFirstGuardianUserId ? request.getFirstGuardianUserId(): null)
                .secondGuardianUserId(validSecondGuardianUserId? request.getSecondGuardianUserId(): null)
                .build();

        final UserAuth userAuth = UserAuth.builder()
                .userDetail(userDetail)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        UserAuth savedUser = userAuthRepository.save(userAuth);
        log.info("Successfully registered user {}", savedUser.getUserDetail());
        return userAuth.getUsername();
    }


    public String signIn(UserSignInRequest request) throws UserAuthException {
        UserAuth userAuth = userAuthRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserAuthException("The User name does not exist"));

        if (!passwordEncoder.matches(request.getPassword(), userAuth.getPassword())) {
            throw new UserAuthException("Invalid pass word provided");
        }

        return tokenUtil.generateToken(userAuth.getUsername());
    }
}
