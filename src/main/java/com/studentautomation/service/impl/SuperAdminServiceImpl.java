package com.studentautomation.service.impl;

import com.studentautomation.dto.request.AdminResetPasswordRequestDTO;
import com.studentautomation.dto.request.CreateAdminRequestDTO;
import com.studentautomation.dto.request.UpdateAdminRequestDTO;
import com.studentautomation.dto.response.AdminResponseDTO;
import com.studentautomation.entity.User;
import com.studentautomation.enums.AccountStatus;
import com.studentautomation.enums.Role;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.UserRepository;
import com.studentautomation.service.SuperAdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for Super Admin operations.
 *
 * Purpose:
 * This class contains business logic that only Super Admin can perform.
 * Includes creating admins and managing their account lifecycle.
 *
 * @author Yashvanth
 */
@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminServiceImpl(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new Admin account.
     *
     * Purpose:
     * This method validates duplicate email, checks password confirmation,
     * encrypts password, assigns ADMIN role, and saves the admin user.
     *
     * @param request admin account creation request
     * @return created admin response details
     */
    @Override
    @Transactional
    public AdminResponseDTO createAdmin(CreateAdminRequestDTO request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new InvalidRequestException("Password and confirm password do not match");
        }

        User admin = new User();
        admin.setName(request.name());
        admin.setEmail(request.email());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole(Role.ADMIN);
        admin.setAccountStatus(AccountStatus.ACTIVE);

        User savedAdmin = userRepository.save(admin);

        return toAdminResponse(savedAdmin);
    }

    /**
     * Gets all admin accounts.
     *
     * Purpose:
     * Returns all users with ADMIN role so Super Admin can
     * see the full list of admins in the system.
     *
     * @return list of all admin user details
     */
    @Override
    @Transactional(readOnly = true)
    public List<AdminResponseDTO> getAllAdmins() {

        return userRepository.findByRole(Role.ADMIN)
                .stream()
                .map(this::toAdminResponse)
                .toList();
    }

    /**
     * Gets one admin by ID.
     *
     * Purpose:
     * Returns a single admin account by their user ID.
     * Validates the user exists and is an admin.
     *
     * @param adminId admin user ID
     * @return admin response details
     */
    @Override
    @Transactional(readOnly = true)
    public AdminResponseDTO getAdminById(Long adminId) {

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new InvalidRequestException("User is not an admin");
        }

        return toAdminResponse(admin);
    }

    /**
     * Updates an admin's profile details.
     *
     * Purpose:
     * Super Admin can update an admin's name and email.
     * Email uniqueness is validated before saving.
     *
     * @param adminId admin user ID
     * @param request updated name and email
     * @return updated admin response details
     */
    @Override
    @Transactional
    public AdminResponseDTO updateAdmin(Long adminId, UpdateAdminRequestDTO request) {

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new InvalidRequestException("User is not an admin");
        }

        if (!admin.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already in use by another account");
        }

        admin.setName(request.name());
        admin.setEmail(request.email());

        User updatedAdmin = userRepository.save(admin);

        return toAdminResponse(updatedAdmin);
    }

    /**
     * Activates an admin account.
     *
     * Purpose:
     * Sets user.accountStatus = ACTIVE so the admin can log in again.
     *
     * @param adminId admin user ID
     */
    @Override
    @Transactional
    public void activateAdmin(Long adminId) {

        User admin = getAdminUserById(adminId);
        admin.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(admin);
    }

    /**
     * Deactivates an admin account.
     *
     * Purpose:
     * Sets user.accountStatus = INACTIVE so the admin can no longer log in.
     *
     * @param adminId admin user ID
     */
    @Override
    @Transactional
    public void deactivateAdmin(Long adminId) {

        User admin = getAdminUserById(adminId);
        admin.setAccountStatus(AccountStatus.INACTIVE);
        userRepository.save(admin);
    }

    /**
     * Blocks an admin account.
     *
     * Purpose:
     * Sets user.accountStatus = BLOCKED.
     * Admin is prevented from logging in immediately.
     *
     * @param adminId admin user ID
     */
    @Override
    @Transactional
    public void blockAdmin(Long adminId) {

        User admin = getAdminUserById(adminId);

        if (admin.getAccountStatus() == AccountStatus.BLOCKED) {
            throw new InvalidRequestException("Admin account is already blocked");
        }

        admin.setAccountStatus(AccountStatus.BLOCKED);
        userRepository.save(admin);
    }

    /**
     * Unblocks a blocked admin account.
     *
     * Purpose:
     * Sets user.accountStatus = ACTIVE so admin can log in again.
     *
     * @param adminId admin user ID
     */
    @Override
    @Transactional
    public void unblockAdmin(Long adminId) {

        User admin = getAdminUserById(adminId);

        if (admin.getAccountStatus() != AccountStatus.BLOCKED) {
            throw new InvalidRequestException("Admin account is not blocked");
        }

        admin.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(admin);
    }

    /**
     * Resets an admin's login password.
     *
     * Purpose:
     * Super Admin sets a new temporary password for the admin.
     *
     * @param adminId admin user ID
     * @param request new password and confirm password
     */
    @Override
    @Transactional
    public void resetAdminPassword(Long adminId, AdminResetPasswordRequestDTO request) {

        User admin = getAdminUserById(adminId);

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new InvalidRequestException("New password and confirm password do not match");
        }

        admin.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(admin);
    }

    /**
     * Helper method to load and validate an admin user by ID.
     *
     * Purpose:
     * Avoids code duplication across account lifecycle methods.
     * Validates the user exists and has ADMIN role.
     *
     * @param adminId admin user ID
     * @return admin User entity
     */
    private User getAdminUserById(Long adminId) {

        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new InvalidRequestException("User is not an admin");
        }

        return user;
    }

    /**
     * Converts a User entity to AdminResponseDTO.
     *
     * @param user admin User entity
     * @return admin response data
     */
    private AdminResponseDTO toAdminResponse(User user) {
        return new AdminResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getAccountStatus(),
                user.getCreatedAt()
        );
    }
}