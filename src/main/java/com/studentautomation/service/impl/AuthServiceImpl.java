package com.studentautomation.service.impl;

import com.studentautomation.dto.request.LoginRequestDTO;
import com.studentautomation.dto.request.RegisterRequestDTO;
import com.studentautomation.dto.response.LoginResponseDTO;
import com.studentautomation.entity.User;
import com.studentautomation.enums.AccountStatus;
import com.studentautomation.enums.Role;
import com.studentautomation.repository.UserRepository;
import com.studentautomation.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.studentautomation.security.JwtService;


/**
 * Service implementation for authentication-related operations.
 *
 * Purpose:
 * This class contains the actual business logic for student registration,
 * teacher registration, and login.
 *
 * Important:
 * Role is not accepted from frontend during public registration.
 * Backend itself assigns STUDENT or TEACHER role based on the API called.
 *
 * @author Yashvanth
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new student user account.
     *
     * Purpose:
     * This method registers the user with STUDENT role.
     * The role is assigned by backend, not from frontend request body.
     *
     * @param request registration details from frontend/Postman
     * @return registered student login response details
     */
    @Override
    public LoginResponseDTO registerStudent(RegisterRequestDTO request) {
        return registerUser(request, Role.STUDENT);
    }

    /**
     * Registers a new teacher user account.
     *
     * Purpose:
     * This method registers the user with TEACHER role.
     * The role is assigned by backend, not from frontend request body.
     *
     * @param request registration details from frontend/Postman
     * @return registered teacher login response details
     */
    @Override
    public LoginResponseDTO registerTeacher(RegisterRequestDTO request) {
        return registerUser(request, Role.TEACHER);
    }

    /**
     * Common private method for registering users.
     *
     * Purpose:
     * This method avoids duplicate code between student and teacher registration.
     * It validates email, checks password confirmation, hashes password,
     * saves user, and returns response.
     *
     * @param request registration details from frontend/Postman
     * @param role role assigned by backend
     * @return registered user login response details
     */
    private LoginResponseDTO registerUser(RegisterRequestDTO request, Role role) {

        try {
            if (userRepository.existsByEmail(request.email())) {
                throw new RuntimeException("Email already exists");
            }

            if (!request.password().equals(request.confirmPassword())) {
                throw new RuntimeException("Password and confirm password do not match");
            }

            User user = new User();
            user.setEmail(request.email());
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setRole(role);
            user.setAccountStatus(AccountStatus.ACTIVE);

            User savedUser = userRepository.save(user);

            return new LoginResponseDTO(
                    savedUser.getEmail(),
                    savedUser.getRole(),
                    null,
                    null
            );

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("User registration failed. Please try again later.");
        }
    }

    /**
     * Logs in an existing user.
     *
     * Purpose:
     * This method checks email, account status, and password.
     * If login is successful, it generates JWT access token.
     *
     * @param request login details from frontend/Postman
     * @return logged-in user response details with JWT access token
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        try {
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));

            if (user.getAccountStatus() != AccountStatus.ACTIVE) {
                throw new RuntimeException("Account is not active");
            }

            if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }

            String accessToken = jwtService.generateAccessToken(user);

            return new LoginResponseDTO(
                    user.getEmail(),
                    user.getRole(),
                    accessToken,
                    null
            );

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Login failed. Please try again later.");
        }
    }
}