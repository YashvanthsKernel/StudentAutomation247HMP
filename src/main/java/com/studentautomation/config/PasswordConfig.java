package com.studentautomation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for password encoding.
 *
 * Purpose:
 * This class provides PasswordEncoder bean to hash passwords
 * before saving them into database.
 *
 * @author Yashvanth
 */
@Configuration
public class PasswordConfig {

    /**
     * Creates BCrypt password encoder bean.
     *
     * @return PasswordEncoder object for hashing and matching passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}