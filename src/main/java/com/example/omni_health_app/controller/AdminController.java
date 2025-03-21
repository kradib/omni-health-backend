package com.example.omni_health_app.controller;


import com.example.omni_health_app.dto.request.AddUserRequest;
import com.example.omni_health_app.dto.request.AdminSignInRequest;
import com.example.omni_health_app.dto.request.UserDetailsUpdateRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.service.AdminService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<AddUserResponseData>> createAdmin(@RequestBody @NonNull AddUserRequest request) throws UserAuthException, BadRequestException {
        log.info("Receive add admin request {}", request);
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



    @PostMapping("/addUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<AddUserResponseData>> addUser(@RequestBody @NonNull AddUserRequest request) throws UserAuthException, BadRequestException {
        log.info("Receive add user request {}", request);
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
    public ResponseEntity<ResponseWrapper<UserSignInResponseData>> signIn(@RequestBody @NonNull AdminSignInRequest request) throws UserAuthException {
        log.info("Received user sign-in request for username: {}", request.getUsername());
        final ResponseWrapper<UserSignInResponseData> responseWrapper = UserSignInResponse.builder()
                .data(adminService.signIn(request))
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

    @DeleteMapping("/{userId}")
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
    public ResponseEntity<ResponseWrapper<UserDetailsWithRoleResponseData>> listUsers(
            @RequestParam(value = "roles", required = false) String roles,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "name", required = false) String name) {
        log.info("Received user listing request with role filter: {}", roles);

        final ResponseWrapper<UserDetailsWithRoleResponseData> responseWrapper = UserDetailsWithRoleResponse.builder()
                .data(adminService.listUsers(roles, page, size, name))
                .responseMetadata(ResponseMetadata.builder()
                        .statusCode(HttpStatus.OK.value())
                        .errorCode(0)
                        .build())
                .build();
        return ResponseEntity.ok(responseWrapper);
    }


}
