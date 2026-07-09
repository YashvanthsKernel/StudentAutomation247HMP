package com.studentautomation.controller;

import com.studentautomation.dto.request.CreateAdminRequestDTO;
import com.studentautomation.dto.response.AdminResponseDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.service.SuperAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for Super Admin APIs.
 *
 * Purpose:
 * This controller receives requests that only Super Admin can perform.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    /**
     * Creates a new Admin account.
     *
     * Purpose:
     * This API is used by Super Admin to create Admin users.
     *
     * @param request admin creation request body
     * @return created admin details
     */
    @PostMapping("/admins")
    public ResponseEntity<ApiResponse<AdminResponseDTO>> createAdmin(
            @Valid @RequestBody CreateAdminRequestDTO request) {

        AdminResponseDTO response = superAdminService.createAdmin(request);

        return ResponseEntity.ok(
                ApiResponse.success("Admin created successfully", response)
        );
    }
}