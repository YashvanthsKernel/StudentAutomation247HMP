package com.studentautomation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class.
 *
 * Purpose:
 * This class creates common beans used across the application.
 *
 * @author Yashvanth
 */
@Configuration
public class AppConfig {

    /**
     * Creates ObjectMapper bean.
     *
     * Purpose:
     * ObjectMapper converts Java objects into JSON response manually,
     * especially inside SecurityConfig error handlers.
     *
     * Note:
     * PasswordEncoder is not created here because PasswordConfig already
     * provides the passwordEncoder bean.
     *
     * @return ObjectMapper object
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}