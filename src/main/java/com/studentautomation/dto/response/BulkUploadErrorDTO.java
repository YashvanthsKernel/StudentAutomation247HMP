package com.studentautomation.dto.response;

/**
 * DTO used to return row-wise Excel upload errors.
 *
 * Purpose:
 * This response helps admin understand which Excel row has a problem
 * and what needs to be corrected.
 *
 * @param rowNumber Excel row number where the error occurred
 * @param field field/column name related to the error
 * @param message clear error message
 */
public record BulkUploadErrorDTO(
        int rowNumber,
        String field,
        String message
) {
}