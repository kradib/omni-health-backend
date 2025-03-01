package com.example.omni_health_app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
@Import(OmniHealthAppApplication.class)  // Import your main application class

@SpringBootTest
class OmniHealthAppApplicationTests {

    @Test
    void contextLoads() {
    }

}
