package com.studentautomation.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for sending teacher details to frontend/Postman.
 *
 * Purpose:
 * This DTO controls what teacher data should be visible outside backend.
 * It avoids exposing full Entity objects directly.
 *
 * @param id teacher ID
 * @param employeeId teacher employee ID
 * @param name teacher full name
 * @param email linked user email
 * @param phoneNo teacher phone number
 * @param department teacher department
 * @param designation teacher designation
 * @param qualification teacher qualification
 * @param experienceYears teacher experience in years
 * @param active whether teacher profile is active
 * @param createdAt teacher created time
 * @param updatedAt teacher updated time
 *
 * @author Yashvanth
 */
public record TeacherResponseDTO(
        Long id,
        String employeeId,
        String name,
        String email,
        String phoneNo,
        String department,
        String designation,
        String qualification,
        Integer experienceYears,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}