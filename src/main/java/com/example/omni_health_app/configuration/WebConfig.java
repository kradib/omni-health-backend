package com.example.omni_health_app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173") // The URL of your frontend
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders(
                        "Content-Type",
                        "Access-Control-Allow-Headers",
                        "Access-Control-Expose-Headers",
                        "Content-Disposition",
                        "Authorization",
                        "X-Requested-With"
                )
                .exposedHeaders("Content-Disposition")
                .allowCredentials(true); // If you want to allow credentials
    }
}
