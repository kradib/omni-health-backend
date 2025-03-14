package com.example.omni_health_app.controller;


import com.example.omni_health_app.dto.request.*;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.service.AdminService;
import com.example.omni_health_app.service.UserAuthService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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



    @PutMapping("/updateUser/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<AddUserResponseData>> updateUser(@PathVariable Long userId, @RequestBody UserDetailsUpdateRequest request) throws UserAuthException, BadRequestException {
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


}
