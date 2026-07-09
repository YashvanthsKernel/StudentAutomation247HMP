package com.studentautomation.service;

import com.studentautomation.dto.request.CreateStudentRequestDTO;
import com.studentautomation.dto.request.CreateTeacherRequestDTO;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.dto.response.TeacherResponseDTO;

/**
 * Service interface for Admin operations.
 *
 * Purpose:
 * This interface defines operations that Admin and Super Admin can perform.
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
}