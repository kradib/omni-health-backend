package com.example.omni_health_app.controller;

import com.example.omni_health_app.dto.request.ForgotPasswordRequest;
import com.example.omni_health_app.dto.request.ResetPasswordRequest;
import com.example.omni_health_app.dto.request.UserSignInRequest;
import com.example.omni_health_app.dto.request.UserSignUpRequest;
import com.example.omni_health_app.dto.response.*;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.service.UserAuthService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserAuthService userAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    void testSignUp() {
        UserSignUpRequest request = UserSignUpRequest.builder()
                .username("testUser")
                .password("password")
                .emailId("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userAuthService.signUp(any(UserSignUpRequest.class))).thenReturn("testUser");

        ResponseEntity<ResponseWrapper<UserSignUpResponseData>> response = userController.signUp(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser", response.getBody().getData().getUsername());
    }

//    @Test
//    void testSignIn() throws UserAuthException {
//        UserSignInRequest request = UserSignInRequest.builder()
//                .username("testUser")
//                .password("password")
//                .build();
//
//        when(userAuthService.signIn(any(UserSignInRequest.class))).thenReturn("testToken");
//
//        ResponseEntity<ResponseWrapper<UserSignInResponseData>> response = userController.signIn(request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("testToken", response.getBody().getData().getAuthToken());
//    }

    @Test
    void testForgotPassword() {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .userName("testUser")
                .build();

        ResponseEntity<ResponseWrapper<ForgotPasswordResponseData>> response = userController.forgotPassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser", response.getBody().getData().getUserName());
        assertEquals(true, response.getBody().getData().isSuccess());
    }

    @Test
    void testResetPassword_Success() throws UserAuthException {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("testToken")
                .newPassword("newPassword")
                .build();

        when(userAuthService.validateResetToken(request.getToken())).thenReturn(true);

        ResponseEntity<ResponseWrapper<ResetPasswordResponseData>> response = userController.resetPassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().getData().isSuccess());
    }

    @Test
    void testResetPassword_InvalidToken() throws UserAuthException {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("invalidToken")
                .newPassword("newPassword")
                .build();

        when(userAuthService.validateResetToken(request.getToken())).thenReturn(false);

        try {
            userController.resetPassword(request);
        } catch (UserAuthException e) {
            assertEquals("Invalid or expired token.", e.getMessage());
        }
    }

}
