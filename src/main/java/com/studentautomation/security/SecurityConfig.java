package com.studentautomation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for the application.
 *
 * Purpose:
 * This class controls which APIs are public and which APIs are protected
 * based on JWT authentication and user roles.
 *
 * @author Yashvanth
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Configures HTTP security rules.
     *
     * Purpose:
     * Auth APIs are public.
     * Student APIs are only for students.
     * Teacher APIs are only for teachers.
     * Admin APIs are for admins and super admins.
     * Super Admin APIs are only for super admins.
     *
     * @param http HttpSecurity object used to configure security
     * @return SecurityFilterChain object
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers("/api/auth/**").permitAll()

                        // Future Super Admin APIs
                        .requestMatchers("/api/super-admin/**")
                        .hasRole("SUPER_ADMIN")

                        // Future Admin APIs
                        .requestMatchers("/api/admin/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")

                        // Future Teacher self APIs
                        .requestMatchers("/api/teacher/**")
                        .hasRole("TEACHER")

                        // Future Student self APIs
                        .requestMatchers("/api/student/**")
                        .hasRole("STUDENT")

                        // Current Teacher CRUD APIs
                        // For now, only Admin and Super Admin should manage teachers
                        .requestMatchers("/api/teachers/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")

                        // Current Student CRUD APIs
                        // For now, only Admin and Super Admin should manage students
                        .requestMatchers("/api/students/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")

                        // Every other API needs login
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * Creates authentication provider.
     *
     * Purpose:
     * This provider tells Spring Security how to load users
     * from database and which password encoder should be used.
     *
     * @return AuthenticationProvider object
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }
}