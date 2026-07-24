package com.studentautomation.service;

import com.studentautomation.dto.request.AdminResetPasswordRequestDTO;
import com.studentautomation.dto.request.CreateAdminRequestDTO;
import com.studentautomation.dto.request.UpdateAdminRequestDTO;
import com.studentautomation.dto.response.AdminResponseDTO;

import java.util.List;

/**
 * Service interface for Super Admin operations.
 *
 * Purpose:
 * This interface defines operations that only Super Admin can perform.
 * Includes full admin lifecycle management.
 *
 * @author Yashvanth
 */
public interface SuperAdminService {

    /**
     * Creates a new Admin account.
     *
     * Purpose:
     * Only Super Admin can create Admin users.
     *
     * @param request admin account creation request
     * @return created admin response details
     */
    AdminResponseDTO createAdmin(CreateAdminRequestDTO request);

    /**
     * Gets all admin accounts.
     *
     * @return list of all admin user details
     */
    List<AdminResponseDTO> getAllAdmins();

    /**
     * Gets one admin by ID.
     *
     * @param adminId admin user ID
     * @return admin response details
     */
    AdminResponseDTO getAdminById(Long adminId);

    /**
     * Updates an admin's profile details.
     *
     * @param adminId admin user ID
     * @param request updated name and email
     * @return updated admin response details
     */
    AdminResponseDTO updateAdmin(Long adminId, UpdateAdminRequestDTO request);

    /**
     * Activates an admin account.
     *
     * @param adminId admin user ID
     */
    void activateAdmin(Long adminId);

    /**
     * Deactivates an admin account.
     *
     * @param adminId admin user ID
     */
    void deactivateAdmin(Long adminId);

    /**
     * Blocks an admin account.
     *
     * @param adminId admin user ID
     */
    void blockAdmin(Long adminId);

    /**
     * Unblocks a blocked admin account.
     *
     * @param adminId admin user ID
     */
    void unblockAdmin(Long adminId);

    /**
     * Resets an admin's login password.
     *
     * @param adminId admin user ID
     * @param request new password and confirm password
     */
    void resetAdminPassword(Long adminId, AdminResetPasswordRequestDTO request);
}