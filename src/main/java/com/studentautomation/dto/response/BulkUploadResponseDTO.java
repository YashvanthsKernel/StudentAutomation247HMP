package com.studentautomation.dto.response;

import java.util.List;

/**
 * DTO used to return the result of Excel bulk upload.
 *
 * Purpose:
 * This response tells how many rows were successfully processed
 * and what errors happened in the uploaded Excel file.
 *
 * @param totalRows total number of rows read from Excel
 * @param successCount number of successfully created records
 * @param failureCount number of failed records
 * @param errors row-wise validation errors
 */
public record BulkUploadResponseDTO(
        int totalRows,
        int successCount,
        int failureCount,
        List<BulkUploadErrorDTO> errors
) {
}