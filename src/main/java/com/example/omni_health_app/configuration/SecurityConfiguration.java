package com.example.omni_health_app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable() // Disable CSRF for simplicity in development
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/user/signup").permitAll() // Allow signup and signin
                .requestMatchers("/api/v1/user/signin").permitAll() // Allow signup and signin
                .anyRequest().authenticated() // Protect all other endpoints
                .and()
                .httpBasic(); // Use basic authentication for testing
        return http.build();
    }
}
