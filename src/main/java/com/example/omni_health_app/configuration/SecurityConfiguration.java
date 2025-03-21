package com.example.omni_health_app.configuration;

import com.example.omni_health_app.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfiguration {

    // use your PC IP address
    private static List<String> allowedOrigins = List.of(
            "http://localhost:5173", "http://192.168.29.234:5173",
            "http://localhost:5174", "http://192.168.29.234:5174",
            "http://localhost:5175", "http://192.168.29.234:5175");


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(allowedOrigins);
                    config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setExposedHeaders(List.of("Content-Disposition"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .requestMatchers("/api/v1/user/signup").permitAll() // Allow signup and signin
                .requestMatchers("/api/v1/user/signin").permitAll()
                .requestMatchers("/api/v1/user/forget-password").permitAll()
                .requestMatchers("/api/v1/user/reset-password").permitAll()
                .requestMatchers("/api/v1/admin/signin").permitAll()
                .requestMatchers("/api/v1/admin/addUser").permitAll()
                .anyRequest().authenticated() // Protect all other endpoints
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(); // Use basic authentication for testing
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
