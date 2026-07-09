package com.studentautomation.enums;

/**
 * Enum representing user roles in the Student Automation system.
 *
 * Purpose:
 * This enum controls what type of user is using the system.
 * It helps in role-based access later when we add JWT security.
 *
 * @author Yashvanth
 */
public enum Role {

    STUDENT,
    TEACHER,
    ADMIN,
    SUPER_ADMIN
}