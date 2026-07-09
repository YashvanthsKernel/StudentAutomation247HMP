package com.studentautomation.dto.response;

import com.studentautomation.enums.AccountStatus;
import com.studentautomation.enums.Role;

import java.time.LocalDateTime;

/**
 * Response DTO for Admin account details.
 *
 * Purpose:
 * This DTO sends safe admin details to frontend.
 * Password will never be sent in response.
 *
 * @param id admin user id
 * @param name admin full name
 * @param email admin email address
 * @param role user role
 * @param accountStatus account status
 * @param createdAt account created time
 *
 * @author Yashvanth
 */
public record AdminResponseDTO(
        Long id,
        String name,
        String email,
        Role role,
        AccountStatus accountStatus,
        LocalDateTime createdAt
) {
}