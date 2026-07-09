package com.studentautomation.service;

import com.studentautomation.dto.request.LoginRequestDTO;
import com.studentautomation.dto.request.RegisterRequestDTO;
import com.studentautomation.dto.response.LoginResponseDTO;

/**
 * Service interface for authentication-related operations.
 *
 * Purpose:
 * This interface defines student registration, teacher registration,
 * and login operations.
 *
 * Actual logic will be written inside AuthServiceImpl.
 *
 * @author Yashvanth
 */
public interface AuthService {

    /**
     * Registers a new student user account.
     *
     * Purpose:
     * This method is used only for student registration.
     * The STUDENT role will be assigned from backend logic,
     * not from frontend request body.
     *
     * @param request registration details from frontend/Postman
     * @return registered student login response details
     */
    LoginResponseDTO registerStudent(RegisterRequestDTO request);

    /**
     * Registers a new teacher user account.
     *
     * Purpose:
     * This method is used only for teacher registration.
     * The TEACHER role will be assigned from backend logic,
     * not from frontend request body.
     *
     * @param request registration details from frontend/Postman
     * @return registered teacher login response details
     */
    LoginResponseDTO registerTeacher(RegisterRequestDTO request);

    /**
     * Logs in an existing user.
     *
     * Purpose:
     * Student, Teacher, Admin, and Super Admin can login
     * if their account already exists and account status is ACTIVE.
     *
     * @param request login details from frontend/Postman
     * @return login response details
     */
    LoginResponseDTO login(LoginRequestDTO request);
}