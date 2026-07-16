package com.studentautomation.dto.response;

import java.util.List;

/**
 * DTO used to return bulk attendance result.
 *
 * Purpose:
 * This response tells how many attendance records were processed,
 * how many were saved, and what validation errors happened.
 *
 * @param totalRecords total number of records sent by teacher
 * @param successCount number of attendance records saved
 * @param failureCount number of failed attendance records
 * @param errors validation errors
 *
 * @author Yashvanth
 */
public record BulkAttendanceResponseDTO(
        int totalRecords,
        int successCount,
        int failureCount,
        List<BulkAttendanceErrorDTO> errors
) {
}