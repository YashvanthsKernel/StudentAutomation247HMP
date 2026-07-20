package com.studentautomation.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentautomation.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
    private final ObjectMapper objectMapper;

    /**
     * Initializes required security dependencies.
     *
     * @param jwtAuthenticationFilter filter used to validate JWT tokens
     * @param userDetailsService service used to load user details
     * @param passwordEncoder encoder used to verify passwords
     * @param objectMapper mapper used to create JSON responses
     */
    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            ObjectMapper objectMapper
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    /**
     * Configures HTTP security rules.
     *
     * @param http HttpSecurity object used to configure security
     * @return configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                /*
                 * Enables the global CORS configuration.
                 */
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );

                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE
                                    );

                                    response.setCharacterEncoding("UTF-8");

                                    objectMapper.writeValue(
                                            response.getWriter(),
                                            ApiResponse.failure(
                                                    "Authentication required. Please login first."
                                            )
                                    );
                                }
                        )

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );

                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE
                                    );

                                    response.setCharacterEncoding("UTF-8");

                                    objectMapper.writeValue(
                                            response.getWriter(),
                                            ApiResponse.failure(
                                                    "You do not have permission to access this API."
                                            )
                                    );
                                }
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Public authentication APIs.
                         */
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        /*
                         * Only SUPER_ADMIN can access these APIs.
                         */
                        .requestMatchers("/api/super-admin/**")
                        .hasRole("SUPER_ADMIN")

                        /*
                         * ADMIN and SUPER_ADMIN can access these APIs.
                         */
                        .requestMatchers("/api/admin/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")

                        /*
                         * Only TEACHER can access teacher self APIs.
                         */
                        .requestMatchers("/api/teacher/**")
                        .hasRole("TEACHER")

                        /*
                         * Only STUDENT can access student self APIs.
                         */
                        .requestMatchers("/api/student/**")
                        .hasRole("STUDENT")

                        /*
                         * ADMIN and SUPER_ADMIN can manage teachers.
                         */
                        .requestMatchers("/api/teachers/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")

                        /*
                         * ADMIN and SUPER_ADMIN can manage students.
                         */
                        .requestMatchers("/api/students/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")

                        /*
                         * All remaining APIs require authentication.
                         */
                        .anyRequest()
                        .authenticated()
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
     * Configures Cross-Origin Resource Sharing.
     *
     * Purpose:
     * Allows frontend applications from any origin
     * to call the backend APIs.
     *
     * @return configured CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        /*
         * Allows requests from every origin.
         */
        configuration.setAllowedOrigins(List.of("*"));

        /*
         * Allows all commonly used HTTP methods.
         */
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        /*
         * Allows headers such as Authorization and Content-Type.
         */
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        /*
         * Applies CORS configuration to all API paths.
         */
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Creates the authentication provider.
     *
     * Purpose:
     * Loads users from the database and verifies
     * passwords using the configured password encoder.
     *
     * @return configured AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }
}