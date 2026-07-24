package com.studentautomation.service;

import com.studentautomation.dto.request.AdminResetPasswordRequestDTO;
import com.studentautomation.dto.request.CreateStudentRequestDTO;
import com.studentautomation.dto.request.CreateTeacherRequestDTO;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.dto.response.TeacherResponseDTO;

/**
 * Service interface for Admin operations.
 *
 * Purpose:
 * This interface defines operations that Admin and Super Admin can perform.
 * This includes creating students/teachers and managing their accounts.
 *
 * @author Yashvanth
 */
public interface AdminService {

    /**
     * Creates a student login account and student profile.
     *
     * @param request student creation request body
     * @return created student details
     */
    StudentResponseDTO createStudent(CreateStudentRequestDTO request);

    /**
     * Creates a teacher login account and teacher profile.
     *
     * @param request teacher creation request body
     * @return created teacher details
     */
    TeacherResponseDTO createTeacher(CreateTeacherRequestDTO request);

    // ─── Student Account Management ──────────────────────────────────────

    /**
     * Activates a student profile and their login account.
     *
     * @param studentId student ID
     */
    void activateStudent(Long studentId);

    /**
     * Deactivates a student profile and their login account.
     *
     * @param studentId student ID
     */
    void deactivateStudent(Long studentId);

    /**
     * Blocks a student login account.
     * Student profile remains, but login is blocked.
     *
     * @param studentId student ID
     */
    void blockStudent(Long studentId);

    /**
     * Unblocks a blocked student login account.
     *
     * @param studentId student ID
     */
    void unblockStudent(Long studentId);

    /**
     * Resets a student's login password.
     *
     * @param studentId student ID
     * @param request   new password and confirm password
     */
    void resetStudentPassword(Long studentId, AdminResetPasswordRequestDTO request);

    // ─── Teacher Account Management ──────────────────────────────────────

    /**
     * Activates a teacher profile and their login account.
     *
     * @param teacherId teacher ID
     */
    void activateTeacher(Long teacherId);

    /**
     * Deactivates a teacher profile and their login account.
     *
     * @param teacherId teacher ID
     */
    void deactivateTeacher(Long teacherId);

    /**
     * Blocks a teacher login account.
     * Teacher profile remains, but login is blocked.
     *
     * @param teacherId teacher ID
     */
    void blockTeacher(Long teacherId);

    /**
     * Unblocks a blocked teacher login account.
     *
     * @param teacherId teacher ID
     */
    void unblockTeacher(Long teacherId);

    /**
     * Resets a teacher's login password.
     *
     * @param teacherId teacher ID
     * @param request   new password and confirm password
     */
    void resetTeacherPassword(Long teacherId, AdminResetPasswordRequestDTO request);
}