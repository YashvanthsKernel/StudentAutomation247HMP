package com.studentautomation.repository;

import com.studentautomation.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Teacher entity.
 *
 * Purpose:
 * This interface is used to perform database operations
 * related to teacher profile and professional details.
 *
 * @author Yashvanth
 */
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * Finds a teacher by employee ID.
     *
     * @param employeeId teacher employee ID
     * @return teacher if found
     */
    Optional<Teacher> findByEmployeeId(String employeeId);

    /**
     * Checks whether a teacher already exists with the given employee ID.
     *
     * @param employeeId teacher employee ID
     * @return true if employee ID exists
     */
    boolean existsByEmployeeId(String employeeId);

    /**
     * Checks whether a user is already linked with a teacher profile.
     *
     * @param userId linked user ID
     * @return true if user is already linked with teacher
     */
    boolean existsByUser_Id(Long userId);

    /**
     * Checks duplicate employee ID while updating teacher.
     *
     * Purpose:
     * It ignores the current teacher ID and checks if another teacher
     * already has the same employee ID.
     *
     * @param employeeId teacher employee ID
     * @param id current teacher ID
     * @return true if another teacher has same employee ID
     */
    boolean existsByEmployeeIdAndIdNot(String employeeId, Long id);

    /**
     * Finds teachers by department.
     *
     * @param department teacher department
     * @return list of teachers from given department
     */
    List<Teacher> findByDepartment(String department);
}