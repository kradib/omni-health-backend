package com.example.omni_health_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class OmniHealthAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmniHealthAppApplication.class, args);
	}

}
