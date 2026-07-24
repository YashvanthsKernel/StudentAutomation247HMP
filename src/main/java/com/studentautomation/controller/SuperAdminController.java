package com.studentautomation.controller;

import com.studentautomation.dto.request.AdminResetPasswordRequestDTO;
import com.studentautomation.dto.request.CreateAdminRequestDTO;
import com.studentautomation.dto.request.UpdateAdminRequestDTO;
import com.studentautomation.dto.response.AdminResponseDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.service.SuperAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for Super Admin APIs.
 *
 * Purpose:
 * This controller receives requests that only Super Admin can perform.
 * Includes full admin account lifecycle management.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    /**
     * Creates a new Admin account.
     *
     * Purpose:
     * This API is used by Super Admin to create Admin users.
     *
     * @param request admin creation request body
     * @return created admin details
     */
    @PostMapping("/admins")
    public ResponseEntity<ApiResponse<AdminResponseDTO>> createAdmin(
            @Valid @RequestBody CreateAdminRequestDTO request) {

        AdminResponseDTO response = superAdminService.createAdmin(request);

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(
                ApiResponse.success("Admin created successfully", response)
        );
    }

    /**
     * Gets all admin accounts.
     *
     * Purpose:
     * Returns a list of all admin users in the system.
     * Super Admin can monitor and manage all admins from here.
     *
     * @return list of admin details
     */
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<List<AdminResponseDTO>>> getAllAdmins() {

        List<AdminResponseDTO> response = superAdminService.getAllAdmins();

        return ResponseEntity.ok(
                ApiResponse.success("Admins fetched successfully", response)
        );
    }

    /**
     * Gets one admin by ID.
     *
     * Purpose:
     * Returns a single admin's details using their user ID.
     *
     * @param adminId admin user ID
     * @return admin details
     */
    @GetMapping("/admins/{adminId}")
    public ResponseEntity<ApiResponse<AdminResponseDTO>> getAdminById(
            @PathVariable Long adminId) {

        AdminResponseDTO response = superAdminService.getAdminById(adminId);

        return ResponseEntity.ok(
                ApiResponse.success("Admin fetched successfully", response)
        );
    }

    /**
     * Updates an admin's profile details.
     *
     * Purpose:
     * Super Admin can update an admin's name and email.
     *
     * @param adminId admin user ID
     * @param request updated name and email
     * @return updated admin details
     */
    @PutMapping("/admins/{adminId}")
    public ResponseEntity<ApiResponse<AdminResponseDTO>> updateAdmin(
            @PathVariable Long adminId,
            @Valid @RequestBody UpdateAdminRequestDTO request) {

        AdminResponseDTO response = superAdminService.updateAdmin(adminId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Admin updated successfully", response)
        );
    }

    /**
     * Activates an admin account.
     *
     * Purpose:
     * Sets user.accountStatus = ACTIVE so the admin can log in again.
     *
     * @param adminId admin user ID
     * @return success message
     */
    @PatchMapping("/admins/{adminId}/activate")
    public ResponseEntity<ApiResponse<Object>> activateAdmin(
            @PathVariable Long adminId) {

        superAdminService.activateAdmin(adminId);

        return ResponseEntity.ok(
                ApiResponse.success("Admin account activated successfully", null)
        );
    }

    /**
     * Deactivates an admin account.
     *
     * Purpose:
     * Sets user.accountStatus = INACTIVE so admin can no longer log in.
     *
     * @param adminId admin user ID
     * @return success message
     */
    @PatchMapping("/admins/{adminId}/deactivate")
    public ResponseEntity<ApiResponse<Object>> deactivateAdmin(
            @PathVariable Long adminId) {

        superAdminService.deactivateAdmin(adminId);

        return ResponseEntity.ok(
                ApiResponse.success("Admin account deactivated successfully", null)
        );
    }

    /**
     * Blocks an admin account.
     *
     * Purpose:
     * Sets user.accountStatus = BLOCKED.
     * Admin is immediately prevented from logging in.
     *
     * @param adminId admin user ID
     * @return success message
     */
    @PatchMapping("/admins/{adminId}/block")
    public ResponseEntity<ApiResponse<Object>> blockAdmin(
            @PathVariable Long adminId) {

        superAdminService.blockAdmin(adminId);

        return ResponseEntity.ok(
                ApiResponse.success("Admin account blocked successfully", null)
        );
    }

    /**
     * Unblocks a blocked admin account.
     *
     * Purpose:
     * Sets user.accountStatus = ACTIVE so admin can log in again.
     *
     * @param adminId admin user ID
     * @return success message
     */
    @PatchMapping("/admins/{adminId}/unblock")
    public ResponseEntity<ApiResponse<Object>> unblockAdmin(
            @PathVariable Long adminId) {

        superAdminService.unblockAdmin(adminId);

        return ResponseEntity.ok(
                ApiResponse.success("Admin account unblocked successfully", null)
        );
    }

    /**
     * Resets an admin's login password.
     *
     * Purpose:
     * Super Admin sets a new temporary password for the admin.
     *
     * @param adminId admin user ID
     * @param request new password request
     * @return success message
     */
    @PostMapping("/admins/{adminId}/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetAdminPassword(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminResetPasswordRequestDTO request) {

        superAdminService.resetAdminPassword(adminId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Admin password reset successfully", null)
        );
    }
}