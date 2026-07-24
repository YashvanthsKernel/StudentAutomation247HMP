package com.studentautomation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Main entry point for the Student Automation System.
 *
 * Purpose:
 * Bootstraps the Spring Boot application.
 * @EnableMethodSecurity enables @PreAuthorize and @PostAuthorize
 * annotations on service and controller methods.
 *
 * @author Yashvanth
 */
@SpringBootApplication
@EnableMethodSecurity
public class StudentAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentAutomationApplication.class, args);
	}

}
