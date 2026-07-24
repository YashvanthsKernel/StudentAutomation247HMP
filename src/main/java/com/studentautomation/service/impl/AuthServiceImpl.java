package com.studentautomation.service.impl;

import com.studentautomation.dto.request.ChangePasswordRequestDTO;
import com.studentautomation.dto.request.ForgotPasswordRequestDTO;
import com.studentautomation.dto.request.LoginRequestDTO;
import com.studentautomation.dto.request.ResetPasswordRequestDTO;
import com.studentautomation.dto.response.AuthTokenResponseDTO;
import com.studentautomation.dto.response.LoginResponseDTO;
import com.studentautomation.dto.response.UserMeResponseDTO;
import com.studentautomation.entity.User;
import com.studentautomation.enums.AccountStatus;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.UserRepository;
import com.studentautomation.repository.PasswordResetTokenRepository;
import com.studentautomation.security.JwtService;
import com.studentautomation.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for authentication-related operations.
 *
 * Purpose:
 * This class contains login logic, token refresh, logout,
 * password management, and user identity operations.
 *
 * Note:
 * Public student and teacher registration has been removed.
 * Final production flow:
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final com.studentautomation.service.AuditLogService auditLogService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           com.studentautomation.service.AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Logs in a user account.
     *
     * Purpose:
     * This method validates user email and password.
     * If valid, it generates both access token and refresh token.
     *
     * Access token:
     * Returned in JSON response and used for protected APIs.
     *
     * Refresh token:
     * Sent to controller so controller can store it inside HttpOnly cookie.
     *
     * @param request login request data containing email and password
     * @return authentication response containing access token and refresh token
     */
    @Override
    public AuthTokenResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidRequestException("Invalid email or password");
        }

        if (user.getAccountStatus() == AccountStatus.BLOCKED) {
            throw new InvalidRequestException("Your account has been blocked. Please contact admin.");
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidRequestException("Account is not active. Please contact admin.");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        auditLogService.log(user.getEmail(), user.getRole().name(), "LOGIN_SUCCESS", "User logged in successfully");

        return new AuthTokenResponseDTO(
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                refreshToken
        );
    }

    /**
     * Refreshes access token using refresh token.
     *
     * Purpose:
     * This method checks whether refresh token is valid.
     * If valid, it extracts user email, loads user from database,
     * and generates a new access token.
     *
     * Important:
     * Refresh token is not returned in response body.
     * It remains inside HttpOnly cookie.
     *
     * @param refreshToken JWT refresh token from cookie
     * @return login response containing new access token
     */
    @Override
    public LoginResponseDTO refreshAccessToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRequestException("Refresh token is missing");
        }

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new InvalidRequestException("Invalid or expired refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidRequestException("Account is not active");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return new LoginResponseDTO(
                user.getEmail(),
                user.getRole().name(),
                newAccessToken
        );
    }

    /**
     * Performs logout for the currently logged-in user.
     *
     * Purpose:
     * Since JWT is stateless, logout is handled client-side by clearing
     * the refresh token cookie. This method is a hook for future
     * token blacklisting or audit logging.
     */
    @Override
    public void logout() {
        /*
         * Stateless JWT logout.
         * Refresh token cookie is cleared by the controller.
         * Token blacklisting can be added here in Phase 6.
         */
    }

    /**
     * Returns the profile of the currently logged-in user.
     *
     * Purpose:
     * Extracts user identity from JWT email claim
     * and returns their basic profile details.
     *
     * @param email email extracted from JWT token
     * @return authenticated user's profile details
     */
    @Override
    @Transactional(readOnly = true)
    public UserMeResponseDTO getMe(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserMeResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getAccountStatus(),
                user.getCreatedAt()
        );
    }

    /**
     * Changes password for the currently logged-in user.
     *
     * Purpose:
     * Validates old password, checks new password confirmation,
     * and updates the hashed password in the database.
     *
     * @param email   email extracted from JWT token
     * @param request contains old password, new password, confirm password
     */
    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequestDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new InvalidRequestException("Current password is incorrect");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new InvalidRequestException("New password and confirm password do not match");
        }

        if (request.oldPassword().equals(request.newPassword())) {
            throw new InvalidRequestException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    /**
     * Initiates forgot password flow by generating a secure reset token.
     *
     * @param request contains user email
     */
    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);

            String token = java.util.UUID.randomUUID().toString().replace("-", "");
            com.studentautomation.entity.PasswordResetToken resetToken =
                    new com.studentautomation.entity.PasswordResetToken(
                            token,
                            user,
                            java.time.LocalDateTime.now().plusMinutes(15)
                    );
            passwordResetTokenRepository.save(resetToken);
        });
    }

    /**
     * Resets password using a valid reset token.
     *
     * @param request contains reset token and new password
     */
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new InvalidRequestException("New password and confirm password do not match");
        }

        com.studentautomation.entity.PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new InvalidRequestException("Invalid or expired password reset token"));

        if (resetToken.isUsed()) {
            throw new InvalidRequestException("Password reset token has already been used");
        }

        if (resetToken.isExpired()) {
            throw new InvalidRequestException("Password reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}