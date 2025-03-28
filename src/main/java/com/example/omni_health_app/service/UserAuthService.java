package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.domain.repositories.UserDetailsRepository;
import com.example.omni_health_app.dto.request.UserDetailsUpdateRequest;
import com.example.omni_health_app.dto.request.UserSignInRequest;
import com.example.omni_health_app.dto.request.UserSignUpRequest;
import com.example.omni_health_app.dto.response.UserSignInResponseData;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

import static com.example.omni_health_app.util.Constants.CACHE_NAME;
import static com.example.omni_health_app.util.Constants.PATIENT_ROLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService {


    private final UserAuthRepository userAuthRepository;
    private final UserDetailsRepository userDetailsRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenUtil tokenUtil;
    private final CacheManager cacheManager;
    private final INotificationService notificationService;

    public String signUp(UserSignUpRequest request) throws UserAuthException {
        if (userAuthRepository.existsByUsername(request.getUsername())) {
            throw new UserAuthException("Username is already taken");
        }
        if (userDetailsRepository.existsByEmail(request.getEmail())) {
            throw new UserAuthException("Email Id belongs to another user");
        }
        if (userDetailsRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAuthException("Phone Number belongs to another user");
        }


        boolean validFirstGuardianUserId = userAuthRepository.existsByUsername(request.getFirstGuardianUserId());
        boolean validSecondGuardianUserId = userAuthRepository.existsByUsername(request.getSecondGuardianUserId());
        log.info("the validity of userid {} is {}, userId {} is {}",
                request.getFirstGuardianUserId(), validFirstGuardianUserId, request.getSecondGuardianUserId(), validSecondGuardianUserId);

        final UserDetail userDetail = UserDetail.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .firstGuardianUserId(validFirstGuardianUserId ? request.getFirstGuardianUserId(): null)
                .secondGuardianUserId(validSecondGuardianUserId? request.getSecondGuardianUserId(): null)
                .dateOfBirth(request.getDateOfBirth())
                .height(request.getHeight())
                .weight(request.getWeight())
                .bloodGroup(request.getBloodGroup())
                .gender(request.getGender())
                .build();

        final UserAuth userAuth = UserAuth.builder()
                .userDetail(userDetail)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(PATIENT_ROLE)
                .build();

        UserAuth savedUser = userAuthRepository.save(userAuth);
        log.info("Successfully registered user {}", savedUser.getUserDetail());
        return userAuth.getUsername();
    }

    public UserDetail updateUserDetails(String userName, UserDetailsUpdateRequest request) throws UserAuthException {
        if (!userAuthRepository.existsByUsername(userName)) {
            throw new UserAuthException("User does not exist");
        }
        final UserAuth userAuth = userAuthRepository.findByUsername(userName).get();
        if (!userAuth.getUserDetail().getEmail().equals(request.getEmail()) && userDetailsRepository.existsByEmail(request.getEmail())) {
            throw new UserAuthException("Email Id belongs to another user");
        }
        if (!userAuth.getUserDetail().getPhoneNumber().equals(request.getPhoneNumber()) && userDetailsRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAuthException("Phone Number belongs to another user");
        }
        boolean validFirstGuardianUserId = userAuthRepository.existsByUsername(request.getFirstGuardianUserId());
        boolean validSecondGuardianUserId = userAuthRepository.existsByUsername(request.getSecondGuardianUserId());
        log.info("the validity of userid {} is {}, userId {} is {}",
                request.getFirstGuardianUserId(), validFirstGuardianUserId,
                request.getSecondGuardianUserId(), validSecondGuardianUserId);

        final UserDetail userDetail = userAuth.getUserDetail();

        userDetail.setEmail(request.getEmail() != null ? request.getEmail() : userDetail.getEmail());
        userDetail.setFirstName(request.getFirstName() != null ? request.getFirstName() : userDetail.getFirstName());
        userDetail.setLastName(request.getLastName() != null ? request.getLastName() : userDetail.getLastName());
        userDetail.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : userDetail.getPhoneNumber());
        userDetail.setMajor(request.getMajor() != null ? request.getMajor() : userDetail.getMajor());
        userDetail.setLocation(request.getLocation() != null ? request.getLocation() : userDetail.getLocation());
        userDetail.setFirstGuardianUserId(validFirstGuardianUserId && userDetail.getFirstGuardianUserId() == null
                ? request.getFirstGuardianUserId() : userDetail.getFirstGuardianUserId());
        userDetail.setSecondGuardianUserId(validSecondGuardianUserId && userDetail.getSecondGuardianUserId() == null
                ? request.getSecondGuardianUserId() : userDetail.getSecondGuardianUserId());
        userDetail.setDateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth() : userDetail.getDateOfBirth());
        userDetail.setWeight(request.getWeight() != null ? request.getWeight() : userDetail.getWeight());
        userDetail.setHeight(request.getHeight() != null ? request.getHeight() : userDetail.getHeight());
        userDetail.setBloodGroup(request.getBloodGroup() != null ? request.getBloodGroup() : userDetail.getBloodGroup());

        userAuth.setUserDetail(userDetail);
        userAuthRepository.save(userAuth);

        log.info("Successfully updated user {}", userDetail);
        return userDetail;
    }


    public UserSignInResponseData signIn(UserSignInRequest request) throws UserAuthException {
        UserAuth userAuth = userAuthRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserAuthException("The User name does not exist"));

        if (!passwordEncoder.matches(request.getPassword(), userAuth.getPassword())) {
            throw new UserAuthException("Invalid pass word provided");
        }

        return UserSignInResponseData.builder()
                .authToken(tokenUtil.generateToken(userAuth.getUsername(),userAuth))
                .userDetail(userAuth.getUserDetail())
                .build();
    }

    public void processForgotPassword(String userName) {
        Optional<UserAuth> userOptional = userAuthRepository.findByUsername(userName);
        if (userOptional.isPresent()) {
            UserAuth userAuth = userOptional.get();
            Random random = new Random();
            int otp = 1000 + random.nextInt(9000);
            Cache cache = cacheManager.getCache(CACHE_NAME);
            cache.put(Integer.toString(otp), userAuth.getUsername());
            notificationService.sendNotification(userAuth.getUserDetail().getEmail(), "Password Reset Request",
                    "Use this code to reset your password: " + otp);
        }
    }

    public boolean validateResetToken(String token) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        String userName = cache.get(token, String.class);
        return userName != null;
    }

    public void resetPassword(String token, String newPassword) throws UserAuthException {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        String userName = cache.get(token, String.class);
        if (userName != null) {
            Optional<UserAuth> userOptional = userAuthRepository.findByUsername(userName);
            if (userOptional.isPresent()) {
                UserAuth userAuth = userOptional.get();
                userAuth.setPassword(passwordEncoder.encode(newPassword)); // Hash password before saving
                userAuthRepository.save(userAuth);
                cache.evict(token);
            } else {
                throw new UserAuthException("Invalid token");
            }
        } else {
            throw new UserAuthException("Token does not exist");
        }
    }



}
