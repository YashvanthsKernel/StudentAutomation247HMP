package com.studentautomation.service.impl;

import com.studentautomation.dto.request.LoginRequestDTO;
import com.studentautomation.dto.request.RegisterRequestDTO;
import com.studentautomation.dto.response.LoginResponseDTO;
import com.studentautomation.entity.User;
import com.studentautomation.enums.AccountStatus;
import com.studentautomation.enums.Role;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.repository.UserRepository;
import com.studentautomation.security.JwtService;
import com.studentautomation.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for authentication-related operations.
 *
 * Purpose:
 * This class contains login logic and temporary registration logic.
 *
 * Important:
 * Student and Teacher public registration should be removed from final production flow.
 * Final flow should be:
 * Super Admin creates Admin.
 * Admin creates Student and Teacher.
 * Student and Teacher only login.
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
     * This is temporary development logic.
     * In final production flow, student should be created by Admin only.
     *
     * @param request registration details from frontend/Postman
     * @return registered student login response details
     */
    @Override
    @Transactional
    public LoginResponseDTO registerStudent(RegisterRequestDTO request) {
        return registerUser(request, Role.STUDENT);
    }

    /**
     * Registers a new teacher user account.
     *
     * Purpose:
     * This is temporary development logic.
     * In final production flow, teacher should be created by Admin only.
     *
     * @param request registration details from frontend/Postman
     * @return registered teacher login response details
     */
    @Override
    @Transactional
    public LoginResponseDTO registerTeacher(RegisterRequestDTO request) {
        return registerUser(request, Role.TEACHER);
    }

    /**
     * Common private method for temporary user registration.
     *
     * Purpose:
     * This method validates email, checks password confirmation,
     * hashes password, saves user, and returns response.
     *
     * @param request registration details
     * @param role role assigned by backend
     * @return registered user login response details
     */
    private LoginResponseDTO registerUser(RegisterRequestDTO request, Role role) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new InvalidRequestException("Password and confirm password do not match");
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
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidRequestException("Account is not active");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponseDTO(
                user.getEmail(),
                user.getRole(),
                accessToken,
                null
        );
    }
}