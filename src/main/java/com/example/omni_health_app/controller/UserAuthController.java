package com.example.omni_health_app.controller;


import com.example.omni_health_app.dto.request.*;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.service.UserAuthService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.omni_health_app.util.UserNameUtil.getCurrentUsername;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseWrapper<UserSignUpResponseData>> signUp(@RequestBody @NonNull UserSignUpRequest request) throws UserAuthException {
        log.info("Receive user sign up request {}", request);
        final ResponseWrapper<UserSignUpResponseData> responseWrapper = UserSignUpResponse.builder()
                .data(UserSignUpResponseData.builder()
                        .username(userAuthService.signUp(request))
                        .build())
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);

    }

    @PatchMapping
    public ResponseEntity<ResponseWrapper<UserUpdateResponseData>> updateProfile(@RequestBody @NonNull UserDetailsUpdateRequest request) throws UserAuthException {
        final String userName = getCurrentUsername();
        log.info("Receive user update up request {} for {}", request, userName);
        final ResponseWrapper<UserUpdateResponseData> responseWrapper = UserUpdateResponse.builder()
                .data(UserUpdateResponseData.builder()
                        .userDetail(userAuthService.updateUserDetails(userName, request))
                        .build())
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseWrapper<UserSignInResponseData>> signIn(@RequestBody @NonNull UserSignInRequest request) throws UserAuthException {
        log.info("Received user sign-in request for username: {}", request.getUsername());
        final ResponseWrapper<UserSignInResponseData> responseWrapper = UserSignInResponse.builder()
                .data(userAuthService.signIn(request))
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ResponseWrapper<ForgotPasswordResponseData>> forgotPassword(@RequestBody @NonNull ForgotPasswordRequest request) {
        log.info("Received user forget password request for username: {}", request.getUserName());
        userAuthService.processForgotPassword(request.getUserName());
        final ResponseWrapper<ForgotPasswordResponseData> responseWrapper = ForgotPasswordResponse.builder()
                .data(ForgotPasswordResponseData.builder()
                        .userName(request.getUserName())
                        .success(true)
                        .build())
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseWrapper<ResetPasswordResponseData>> resetPassword(@RequestBody @NonNull ResetPasswordRequest request) throws UserAuthException {
        boolean isValidToken = userAuthService.validateResetToken(request.getToken());
        if (!isValidToken) {
            throw new UserAuthException("Invalid or expired token.");
        }
        userAuthService.resetPassword(request.getToken(), request.getNewPassword());
        final ResponseWrapper<ResetPasswordResponseData> responseWrapper = ResetPasswordResponse.builder()
                .data(ResetPasswordResponseData.builder()
                        .success(true)
                        .build())
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);
    }



}
