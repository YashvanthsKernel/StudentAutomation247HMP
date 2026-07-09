package com.studentautomation.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for sending student details to frontend/Postman.
 *
 * Purpose:
 * This DTO controls what student data should be visible outside backend.
 * It avoids exposing full Entity objects directly.
 *
 * @param id student ID
 * @param regNo student register number
 * @param name student full name
 * @param email linked user email
 * @param phoneNo student phone number
 * @param department student department
 * @param semester student semester
 * @param section student section
 * @param academicYear academic year
 * @param active whether student profile is active
 * @param createdAt student created time
 * @param updatedAt student updated time
 *
 * @author Yashvanth
 */
public record StudentResponseDTO(
        Long id,
        String regNo,
        String name,
        String email,
        String phoneNo,
        String department,
        Integer semester,
        String section,
        String academicYear,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}