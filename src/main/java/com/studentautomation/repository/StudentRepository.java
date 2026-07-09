package com.studentautomation.repository;

import com.studentautomation.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Student entity.
 *
 * Purpose:
 * This interface is used to perform database operations
 * related to student academic and profile details.
 *
 * Note:
 * We do not write implementation for this interface.
 * Spring Data JPA automatically creates the implementation.
 *
 * @author Yashvanth
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Finds a student by register number.
     *
     * @param regNo student register number
     * @return student if found
     */
    Optional<Student> findByRegNo(String regNo);

    /**
     * Checks whether a student already exists with the given register number.
     *
     * @param regNo student register number
     * @return true if register number exists
     */
    boolean existsByRegNo(String regNo);

    /**
     * Checks whether a user is already linked with a student profile.
     *
     * @param userId linked user ID
     * @return true if user is already linked with student
     */
    boolean existsByUser_Id(Long userId);

    /**
     * Checks duplicate register number while updating student.
     *
     * Purpose:
     * It ignores the current student ID and checks if another student
     * already has the same register number.
     *
     * @param regNo student register number
     * @param id current student ID
     * @return true if another student has same register number
     */
    boolean existsByRegNoAndIdNot(String regNo, Long id);

    /**
     * Finds students by department.
     *
     * @param department student department
     * @return list of students from given department
     */
    List<Student> findByDepartment(String department);

    /**
     * Finds students by department and semester.
     *
     * @param department student department
     * @param semester student semester
     * @return list of students from given department and semester
     */
    List<Student> findByDepartmentAndSemester(String department, Integer semester);
}