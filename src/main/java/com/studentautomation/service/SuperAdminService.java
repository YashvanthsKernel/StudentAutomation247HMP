package com.studentautomation.service;

import com.studentautomation.dto.request.CreateAdminRequestDTO;
import com.studentautomation.dto.response.AdminResponseDTO;

/**
 * Service interface for Super Admin operations.
 *
 * Purpose:
 * This interface defines operations that only Super Admin can perform.
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
}