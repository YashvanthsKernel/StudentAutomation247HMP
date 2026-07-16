package com.studentautomation.service;

import com.studentautomation.dto.response.BulkUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for Excel upload operations.
 *
 * Purpose:
 * This service handles bulk creation of students and teachers
 * from uploaded Excel files.
 *
 * @author Yashvanth
 */
public interface ExcelUploadService {

    /**
     * Uploads student Excel file and creates student profiles.
     *
     * @param file Excel file uploaded by admin
     * @return bulk upload result
     */
    BulkUploadResponseDTO uploadStudents(MultipartFile file);

    /**
     * Uploads teacher Excel file and creates teacher profiles.
     *
     * @param file Excel file uploaded by admin
     * @return bulk upload result
     */
    BulkUploadResponseDTO uploadTeachers(MultipartFile file);
}