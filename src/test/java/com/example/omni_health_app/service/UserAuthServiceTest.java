package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.entity.UserDetail;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.request.UserSignInRequest;
import com.example.omni_health_app.dto.request.UserSignUpRequest;
import com.example.omni_health_app.exception.UserAuthException;
import com.example.omni_health_app.util.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.omni_health_app.util.Constants.CACHE_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UserAuthServiceTest {

    @InjectMocks
    private UserAuthService userAuthService;

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenUtil tokenUtil;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private INotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp_Success() throws UserAuthException {
        UserSignUpRequest request = UserSignUpRequest.builder()
                .username("testUser")
                .password("password")
                .emailId("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .build();

        when(userAuthRepository.existsByUsername(eq(request.getUsername()))).thenReturn(false);
        when(passwordEncoder.encode(eq(request.getPassword()))).thenReturn("hashedPassword");
        when(userAuthRepository.save(any(UserAuth.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = userAuthService.signUp(request);

        assertNotNull(result);
        assertEquals(request.getUsername(), result);
        verify(userAuthRepository, times(1)).save(any(UserAuth.class));
    }

    @Test
    void testSignUp_UsernameAlreadyExists() {
        UserSignUpRequest request = UserSignUpRequest.builder()
                .username("testUser")
                .password("password")
                .lastName("bar")
                .emailId("test@gmail.com")
                .build();

        when(userAuthRepository.existsByUsername(eq(request.getUsername()))).thenReturn(true);

        assertThrows(UserAuthException.class, () -> userAuthService.signUp(request));
    }

    @Test
    void testSignIn_Success() throws UserAuthException {
        UserSignInRequest request = UserSignInRequest.builder()
                .username("testUser")
                .password("password")
                .build();

        UserAuth userAuth = UserAuth.builder()
                .username(request.getUsername())
                .password("hashedPassword")
                .roles("ROLE_PATIENT") // Ensure this exists
                .build();

        when(userAuthRepository.findByUsername(eq(request.getUsername()))).thenReturn(Optional.of(userAuth));
        when(passwordEncoder.matches(eq(request.getPassword()), eq(userAuth.getPassword()))).thenReturn(true);
        
        // Pass userAuth to generateToken
        when(tokenUtil.generateToken(eq(request.getUsername()), eq(userAuth))).thenReturn("testToken");

        String token = userAuthService.signIn(request);

        assertNotNull(token);
        assertEquals("testToken", token);
    }

    @Test
    void testSignIn_InvalidPassword() {
        UserSignInRequest request = UserSignInRequest.builder()
                .username("testUser")
                .password("wrongPassword")
                .build();

        UserAuth userAuth = UserAuth.builder()
                .username(request.getUsername())
                .password("hashedPassword")
                .roles("ROLE_PATIENT")
                .build();

        when(userAuthRepository.findByUsername(eq(request.getUsername()))).thenReturn(Optional.of(userAuth));
        when(passwordEncoder.matches(eq(request.getPassword()), eq(userAuth.getPassword()))).thenReturn(false);

        assertThrows(UserAuthException.class, () -> userAuthService.signIn(request));
    }

    @Test
    void testProcessForgotPassword() {
        String userName = "testUser";
        UserAuth userAuth = mock(UserAuth.class);
        UserDetail userDetail = mock(UserDetail.class);
        Cache cache = mock(Cache.class);

        when(userAuthRepository.findByUsername(eq(userName))).thenReturn(Optional.of(userAuth));
        when(userAuth.getUserDetail()).thenReturn(userDetail);
        when(userAuth.getUsername()).thenReturn(userName);
        when(userDetail.getEmail()).thenReturn("test@example.com");
        when(cacheManager.getCache(eq(CACHE_NAME))).thenReturn(cache);

        userAuthService.processForgotPassword(userName);

        verify(notificationService, times(1)).sendNotification(eq("test@example.com"), eq("Password Reset Request"), any(String.class));
        verify(cache, times(1)).put(any(String.class), eq(userName));
    }

    @Test
    void testValidateResetToken_Valid() {
        String token = "testToken";
        Cache cache = mock(Cache.class);

        when(cacheManager.getCache(eq(CACHE_NAME))).thenReturn(cache);
        when(cache.get(eq(token), eq(String.class))).thenReturn("testUser");

        boolean result = userAuthService.validateResetToken(token);

        assertTrue(result);
    }

    @Test
    void testValidateResetToken_Invalid() {
        String token = "invalidToken";
        Cache cache = mock(Cache.class);

        when(cacheManager.getCache(eq(CACHE_NAME))).thenReturn(cache);
        when(cache.get(eq(token), eq(String.class))).thenReturn(null);

        boolean result = userAuthService.validateResetToken(token);

        assertFalse(result);
    }

    @Test
    void testResetPassword_Success() throws UserAuthException {
        String token = "testToken";
        String newPassword = "newPassword";
        String userName = "testUser";

        Cache cache = mock(Cache.class);
        UserAuth userAuth = mock(UserAuth.class);

        when(cacheManager.getCache(eq(CACHE_NAME))).thenReturn(cache);
        when(cache.get(eq(token), eq(String.class))).thenReturn(userName);
        when(userAuthRepository.findByUsername(eq(userName))).thenReturn(Optional.of(userAuth));
        when(passwordEncoder.encode(eq(newPassword))).thenReturn("hashedNewPassword");

        userAuthService.resetPassword(token, newPassword);

        verify(userAuth).setPassword(eq("hashedNewPassword"));
        verify(userAuthRepository, times(1)).save(userAuth);
        verify(cache, times(1)).evict(eq(token));
    }

    @Test
    void testResetPassword_InvalidToken() {
        String token = "invalidToken";
        String newPassword = "newPassword";
        String userName = "test";

        Cache cache = mock(Cache.class);

        when(cacheManager.getCache(eq(CACHE_NAME))).thenReturn(cache);
        when(cache.get(eq(token), eq(String.class))).thenReturn(userName);
        when(userAuthRepository.findByUsername(userName)).thenReturn(Optional.empty());

        assertThrows(UserAuthException.class, () -> userAuthService.resetPassword(token, newPassword));
    }
}
