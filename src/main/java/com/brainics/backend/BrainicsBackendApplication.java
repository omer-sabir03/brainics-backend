package com.brainics.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// UserDetailsServiceAutoConfiguration is excluded so Spring does not create a
// default "user" account with a random password. Real login arrives in Phase 2.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class BrainicsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrainicsBackendApplication.class, args);
    }
}
