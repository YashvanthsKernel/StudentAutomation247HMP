package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.BulkUploadResponseDTO;
import com.studentautomation.service.ExcelUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for admin Excel upload APIs.
 *
 * Purpose:
 * This controller allows ADMIN users to upload Excel files
 * and create students or teachers in bulk.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/admin")
public class AdminExcelUploadController {

    private final ExcelUploadService excelUploadService;

    public AdminExcelUploadController(ExcelUploadService excelUploadService) {
        this.excelUploadService = excelUploadService;
    }

    /**
     * Uploads student Excel file.
     *
     * Purpose:
     * This API receives a multipart/form-data Excel file
     * and creates multiple student accounts and profiles.
     *
     * @param file uploaded Excel file with student details
     * @return bulk upload result
     */
    @PostMapping(
            value = "/students/upload-excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<BulkUploadResponseDTO>> uploadStudentsExcel(
            @RequestParam("file") MultipartFile file
    ) {
        BulkUploadResponseDTO response = excelUploadService.uploadStudents(file);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Student Excel processed successfully", response)
        );
    }

    /**
     * Uploads teacher Excel file.
     *
     * Purpose:
     * This API receives a multipart/form-data Excel file
     * and creates multiple teacher accounts and profiles.
     *
     * @param file uploaded Excel file with teacher details
     * @return bulk upload result
     */
    @PostMapping(
            value = "/teachers/upload-excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<BulkUploadResponseDTO>> uploadTeachersExcel(
            @RequestParam("file") MultipartFile file
    ) {
        BulkUploadResponseDTO response = excelUploadService.uploadTeachers(file);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Teacher Excel processed successfully", response)
        );
    }
}