package com.studentautomation.repository;

import com.studentautomation.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Student entity.
 *
 * Purpose:
 * This interface is used to perform database operations related to
 * student academic and profile details.
 *
 * Note:
 * We do not write implementation for this interface.
 * Spring Data JPA automatically creates the implementation at runtime.
 *
 * @author Yashvanth
 */
public interface StudentRepository extends JpaRepository<Student, Long>,
        JpaSpecificationExecutor<Student> {

    /**
     * Finds a student by register number.
     *
     * Purpose:
     * This method is used when we need to fetch a student profile
     * using the student's unique register number.
     *
     * @param regNo student register number
     * @return student if found
     */
    Optional<Student> findByRegNo(String regNo);

    /**
     * Checks whether a student already exists with the given register number.
     *
     * Purpose:
     * This method prevents creating duplicate student profiles
     * with the same register number.
     *
     * @param regNo student register number
     * @return true if register number already exists
     */
    boolean existsByRegNo(String regNo);

    /**
     * Checks whether another student already has the same register number.
     *
     * Purpose:
     * This method is mainly used while updating a student.
     * It ignores the current student ID and checks if any other student
     * already has the same register number.
     *
     * @param regNo student register number
     * @param id current student ID
     * @return true if another student has the same register number
     */
    boolean existsByRegNoAndIdNot(String regNo, Long id);

    /**
     * Checks whether a user account is already linked with a student profile.
     *
     * Purpose:
     * This prevents linking the same User account to multiple
     * Student profiles.
     *
     * @param userId linked user ID
     * @return true if the user is already linked with a student profile
     */
    boolean existsByUser_Id(Long userId);

    /**
     * Finds a student profile using the linked user email.
     *
     * Purpose:
     * This is used for logged-in student APIs like:
     * GET /api/student/me
     *
     * The email comes from the JWT token, then we use that email
     * to find the Student profile linked to that User account.
     *
     * @param email logged-in user's email
     * @return student profile if found
     */
    Optional<Student> findByUser_Email(String email);

    /**
     * Finds students by department.
     *
     * Purpose:
     * This method is useful for admin-level filtering and reports.
     *
     * @param department student department
     * @return list of students from the given department
     */
    List<Student> findByDepartment(String department);

    /**
     * Finds students by department and semester.
     *
     * Purpose:
     * This method is useful for admin reports, attendance filtering,
     * marks filtering, and academic batch-wise operations.
     *
     * @param department student department
     * @param semester student semester
     * @return list of students from the given department and semester
     */
    List<Student> findByDepartmentAndSemester(String department, Integer semester);
}
