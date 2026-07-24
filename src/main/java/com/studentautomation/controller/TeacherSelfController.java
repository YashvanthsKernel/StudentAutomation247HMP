package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.dto.response.TeacherResponseDTO;
import com.studentautomation.dto.response.TeacherSubjectResponseDTO;
import com.studentautomation.service.TeacherService;
import com.studentautomation.service.TeacherSubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for logged-in Teacher self APIs.
 *
 * Purpose:
 * Allows a teacher to access only their own profile, subjects, sections and student rosters.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherSelfController {

    private final TeacherService teacherService;
    private final TeacherSubjectService teacherSubjectService;

    public TeacherSelfController(TeacherService teacherService, TeacherSubjectService teacherSubjectService) {
        this.teacherService = teacherService;
        this.teacherSubjectService = teacherSubjectService;
    }

    /**
     * Gets the logged-in teacher's own profile.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();
        TeacherResponseDTO response = teacherService.getMyProfile(email);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher profile fetched successfully", response)
        );
    }

    /**
     * Gets subjects assigned to the logged-in teacher.
     */
    @GetMapping("/subjects/me")
    public ResponseEntity<ApiResponse<List<TeacherSubjectResponseDTO>>> getMySubjects(
            Authentication authentication) {

        String email = authentication.getName();
        List<TeacherSubjectResponseDTO> response = teacherSubjectService.getAssignmentsByTeacherEmail(email);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher assigned subjects fetched successfully", response)
        );
    }

    /**
     * Gets sections for a specific assigned subject.
     */
    @GetMapping("/subjects/{subjectId}/sections")
    public ResponseEntity<ApiResponse<List<String>>> getSectionsForSubject(
            @PathVariable Long subjectId,
            Authentication authentication) {

        String email = authentication.getName();
        List<String> response = teacherSubjectService.getSectionsForTeacherSubject(email, subjectId);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher subject sections fetched successfully", response)
        );
    }

    /**
     * Gets students enrolled in a specific assigned subject.
     */
    @GetMapping("/subjects/{subjectId}/students")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getStudentsForSubject(
            @PathVariable Long subjectId,
            Authentication authentication) {

        String email = authentication.getName();
        List<StudentResponseDTO> response = teacherSubjectService.getStudentsForTeacherSubject(email, subjectId);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher subject students fetched successfully", response)
        );
    }
}