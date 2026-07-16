package com.studentautomation.dto.response;

/**
 * DTO used to return row-wise errors during bulk attendance marking.
 *
 * Purpose:
 * This response helps teacher understand which student record failed
 * and why attendance was not saved.
 *
 * @param studentId student ID related to the error
 * @param field field where error happened
 * @param message clear error message
 *
 * @author Yashvanth
 */
public record BulkAttendanceErrorDTO(
        Long studentId,
        String field,
        String message
) {
}