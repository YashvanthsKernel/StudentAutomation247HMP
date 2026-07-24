package com.studentautomation.controller;

import com.studentautomation.dto.request.AdminResetPasswordRequestDTO;
import com.studentautomation.dto.request.CreateStudentRequestDTO;
import com.studentautomation.dto.request.CreateTeacherRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.dto.response.TeacherResponseDTO;
import com.studentautomation.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for Admin APIs.
 *
 * Purpose:
 * This controller receives requests that Admin and Super Admin can perform.
 * Includes student/teacher creation and full account lifecycle management.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final com.studentautomation.service.StudentService studentService;
    private final com.studentautomation.service.TeacherService teacherService;

    public AdminController(AdminService adminService,
                           com.studentautomation.service.StudentService studentService,
                           com.studentautomation.service.TeacherService teacherService) {
        this.adminService = adminService;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<java.util.List<StudentResponseDTO>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.success("Students fetched successfully", studentService.getAllStudents()));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success("Student fetched successfully", studentService.getStudentById(studentId)));
    }

    @GetMapping("/teachers")
    public ResponseEntity<ApiResponse<java.util.List<TeacherResponseDTO>>> getAllTeachers() {
        return ResponseEntity.ok(ApiResponse.success("Teachers fetched successfully", teacherService.getAllTeachers()));
    }

    @GetMapping("/teachers/{teacherId}")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> getTeacherById(@PathVariable Long teacherId) {
        return ResponseEntity.ok(ApiResponse.success("Teacher fetched successfully", teacherService.getTeacherById(teacherId)));
    }

    // ─── Student APIs ─────────────────────────────────────────────────────

    /**
     * Creates a new Student account and profile.
     *
     * Purpose:
     * This API is used by Admin or Super Admin to create students.
     * Student cannot self-register.
     *
     * @param request student account and profile creation request
     * @return created student details
     */
    @PostMapping("/students")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
            @Valid @RequestBody CreateStudentRequestDTO request) {

        StudentResponseDTO response = adminService.createStudent(request);

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(
                ApiResponse.success("Student created successfully", response)
        );
    }

    /**
     * Activates a student account and profile.
     *
     * Purpose:
     * Sets student.active = true and user.accountStatus = ACTIVE.
     * The student can log in again after activation.
     *
     * @param studentId student ID to activate
     * @return success message
     */
    @PatchMapping("/students/{studentId}/activate")
    public ResponseEntity<ApiResponse<Object>> activateStudent(
            @PathVariable Long studentId) {

        adminService.activateStudent(studentId);

        return ResponseEntity.ok(
                ApiResponse.success("Student account activated successfully", null)
        );
    }

    /**
     * Deactivates a student account and profile.
     *
     * Purpose:
     * Sets student.active = false and user.accountStatus = INACTIVE.
     * The student can no longer log in after deactivation.
     *
     * @param studentId student ID to deactivate
     * @return success message
     */
    @PatchMapping("/students/{studentId}/deactivate")
    public ResponseEntity<ApiResponse<Object>> deactivateStudent(
            @PathVariable Long studentId) {

        adminService.deactivateStudent(studentId);

        return ResponseEntity.ok(
                ApiResponse.success("Student account deactivated successfully", null)
        );
    }

    /**
     * Blocks a student's login account.
     *
     * Purpose:
     * Sets user.accountStatus = BLOCKED.
     * Student profile remains, but login is blocked immediately.
     * Use this for disciplinary actions or security concerns.
     *
     * @param studentId student ID to block
     * @return success message
     */
    @PatchMapping("/students/{studentId}/block")
    public ResponseEntity<ApiResponse<Object>> blockStudent(
            @PathVariable Long studentId) {

        adminService.blockStudent(studentId);

        return ResponseEntity.ok(
                ApiResponse.success("Student account blocked successfully", null)
        );
    }

    /**
     * Unblocks a blocked student's login account.
     *
     * Purpose:
     * Sets user.accountStatus = ACTIVE so student can log in again.
     *
     * @param studentId student ID to unblock
     * @return success message
     */
    @PatchMapping("/students/{studentId}/unblock")
    public ResponseEntity<ApiResponse<Object>> unblockStudent(
            @PathVariable Long studentId) {

        adminService.unblockStudent(studentId);

        return ResponseEntity.ok(
                ApiResponse.success("Student account unblocked successfully", null)
        );
    }

    /**
     * Resets a student's login password.
     *
     * Purpose:
     * Admin sets a new temporary password for the student.
     *
     * @param studentId student ID
     * @param request   new password request
     * @return success message
     */
    @PostMapping("/students/{studentId}/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetStudentPassword(
            @PathVariable Long studentId,
            @Valid @RequestBody AdminResetPasswordRequestDTO request) {

        adminService.resetStudentPassword(studentId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Student password reset successfully", null)
        );
    }

    // ─── Teacher APIs ─────────────────────────────────────────────────────

    /**
     * Creates a new Teacher account and profile.
     *
     * Purpose:
     * This API is used by Admin or Super Admin to create teachers.
     * Teacher cannot self-register.
     *
     * @param request teacher account and profile creation request
     * @return created teacher details
     */
    @PostMapping("/teachers")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> createTeacher(
            @Valid @RequestBody CreateTeacherRequestDTO request) {

        TeacherResponseDTO response = adminService.createTeacher(request);

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(
                ApiResponse.success("Teacher created successfully", response)
        );
    }

    /**
     * Activates a teacher account and profile.
     *
     * Purpose:
     * Sets teacher.active = true and user.accountStatus = ACTIVE.
     * The teacher can log in again after activation.
     *
     * @param teacherId teacher ID to activate
     * @return success message
     */
    @PatchMapping("/teachers/{teacherId}/activate")
    public ResponseEntity<ApiResponse<Object>> activateTeacher(
            @PathVariable Long teacherId) {

        adminService.activateTeacher(teacherId);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher account activated successfully", null)
        );
    }

    /**
     * Deactivates a teacher account and profile.
     *
     * Purpose:
     * Sets teacher.active = false and user.accountStatus = INACTIVE.
     * The teacher can no longer log in after deactivation.
     *
     * @param teacherId teacher ID to deactivate
     * @return success message
     */
    @PatchMapping("/teachers/{teacherId}/deactivate")
    public ResponseEntity<ApiResponse<Object>> deactivateTeacher(
            @PathVariable Long teacherId) {

        adminService.deactivateTeacher(teacherId);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher account deactivated successfully", null)
        );
    }

    /**
     * Blocks a teacher's login account.
     *
     * Purpose:
     * Sets user.accountStatus = BLOCKED.
     * Teacher profile remains, but login is blocked immediately.
     *
     * @param teacherId teacher ID to block
     * @return success message
     */
    @PatchMapping("/teachers/{teacherId}/block")
    public ResponseEntity<ApiResponse<Object>> blockTeacher(
            @PathVariable Long teacherId) {

        adminService.blockTeacher(teacherId);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher account blocked successfully", null)
        );
    }

    /**
     * Unblocks a blocked teacher's login account.
     *
     * Purpose:
     * Sets user.accountStatus = ACTIVE so teacher can log in again.
     *
     * @param teacherId teacher ID to unblock
     * @return success message
     */
    @PatchMapping("/teachers/{teacherId}/unblock")
    public ResponseEntity<ApiResponse<Object>> unblockTeacher(
            @PathVariable Long teacherId) {

        adminService.unblockTeacher(teacherId);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher account unblocked successfully", null)
        );
    }

    /**
     * Resets a teacher's login password.
     *
     * Purpose:
     * Admin sets a new temporary password for the teacher.
     *
     * @param teacherId teacher ID
     * @param request   new password request
     * @return success message
     */
    @PostMapping("/teachers/{teacherId}/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetTeacherPassword(
            @PathVariable Long teacherId,
            @Valid @RequestBody AdminResetPasswordRequestDTO request) {

        adminService.resetTeacherPassword(teacherId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher password reset successfully", null)
        );
    }
}