package com.taskflow.config;

import com.taskflow.model.User;
import com.taskflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Registration always creates USER accounts, so the first ADMIN
 * has to come from somewhere: this runs once at startup and creates
 * it if missing. Password comes from the ADMIN_PASSWORD env var.
 */
@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                @Value("${admin.password}") String adminPassword) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@taskflow.local");
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(User.Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
