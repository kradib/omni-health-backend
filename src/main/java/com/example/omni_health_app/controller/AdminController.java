package com.example.omni_health_app.controller;


import com.example.omni_health_app.dto.request.ForgotPasswordRequest;
import com.example.omni_health_app.dto.request.ResetPasswordRequest;
import com.example.omni_health_app.dto.request.UpdateUserRequest;
import com.example.omni_health_app.dto.request.UserSignInRequest;
import com.example.omni_health_app.dto.request.AddUserRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.service.UserAuthService;
import com.example.omni_health_app.service.AdminService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Slf4j
public class AdminController {

    private final UserAuthService userAuthService;
    private final AdminService adminService;


    @PostMapping("/addUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<AddUserResponseData>> signUp(@RequestBody @NonNull AddUserRequest request) throws UserAuthException {
        log.info("Receive add userrequest {}", request);
        final ResponseWrapper<AddUserResponseData> responseWrapper = AddUserResponse.builder()
                .data(AddUserResponseData.builder()
                        .userDetail(adminService.addUser(request))
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

    @PutMapping("/updateUser/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<AddUserResponseData>> updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest request) throws UserAuthException {
        log.info("Received update user request for userId {}: {}", userId, request);
        final ResponseWrapper<AddUserResponseData> responseWrapper = AddUserResponse.builder()
                .data(AddUserResponseData.builder()
                        .userDetail(adminService.updateUser(userId, request))
                        .build())
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

    @DeleteMapping("/deleteUser/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<Void>> deleteUser(@PathVariable Long userId) throws UserAuthException {
        log.info("Received delete user request for userId {}", userId);
        adminService.deleteUser(userId);

        ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>();
        ResponseMetadata responseMetadata = new ResponseMetadata();
        responseMetadata.setStatusCode(HttpStatus.OK.value());
        responseMetadata.setErrorCode(0);

        // responseWrapper.ResponseMetadata(responseMetadata);

        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/listUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<List<UserDetailWithRoles>>> listUsers(
            @RequestParam(value = "role", required = false) String role) {
        log.info("Received user listing request with role filter: {}", role);
        List<UserDetailWithRoles> users = adminService.listUsers(role);

        ResponseMetadata metadata = new ResponseMetadata("1234", null, HttpStatus.OK.value(), 0);

        ResponseWrapper<List<UserDetailWithRoles>> responseWrapper = new ResponseWrapper<>(users, metadata);

        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<GetAllAppointmentResponseData>> getAllAppointmentSchedule(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "doctor", required = false) String doctor,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws BadRequestException {

        log.info("Receive get all appointments from {} to {} for {}", startDate, endDate, doctor);

        final Pageable pageable = PageRequest.of(page, size);


        LocalDateTime startDateTime = (startDate != null && !startDate.isEmpty()) ? LocalDateTime.parse(startDate) : null;
        LocalDateTime endDateTime = (endDate != null && !endDate.isEmpty()) ? LocalDateTime.parse(endDate) : null;


        final ResponseWrapper<GetAllAppointmentResponseData> responseWrapper = GetAllAppointmentResponse.builder()
                .data(adminService.getAllAppointmentSchedule(doctor, startDateTime, endDateTime, pageable))
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();

        return ResponseEntity.ok(responseWrapper);
    }


}
