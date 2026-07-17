package com.studentautomation.repository;

import com.studentautomation.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Subject entity.
 *
 * Purpose:
 * Handles database operations related to academic subjects.
 *
 * Spring Data JPA automatically generates the implementation
 * for the query methods declared in this interface.
 *
 * @author Yashvanth
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * Finds a subject using its unique subject code.
     *
     * The search ignores uppercase and lowercase differences.
     *
     * Example:
     * CS301 and cs301 are treated as the same code.
     *
     * @param subjectCode unique subject code
     * @return subject when found
     */
    Optional<Subject> findBySubjectCodeIgnoreCase(String subjectCode);

    /**
     * Checks whether a subject already exists using the given subject code.
     *
     * This method will be used to prevent duplicate subjects
     * while creating a new subject.
     *
     * @param subjectCode unique subject code
     * @return true when the subject code already exists
     */
    boolean existsBySubjectCodeIgnoreCase(String subjectCode);

    /**
     * Checks whether another subject uses the same subject code.
     *
     * The current subject ID is excluded from the check.
     * This method will be used while updating a subject.
     *
     * @param subjectCode unique subject code
     * @param id subject ID that should be excluded
     * @return true when another subject has the same code
     */
    boolean existsBySubjectCodeIgnoreCaseAndIdNot(
            String subjectCode,
            Long id
    );

    /**
     * Finds all active subjects belonging to a department and semester.
     *
     * Results are sorted alphabetically using the subject name.
     *
     * @param department department name
     * @param semester semester number
     * @return active subjects matching the department and semester
     */
    List<Subject> findByDepartmentIgnoreCaseAndSemesterAndActiveTrueOrderBySubjectNameAsc(
            String department,
            Integer semester
    );

    /**
     * Finds all active subjects.
     *
     * Results are sorted alphabetically using the subject name.
     *
     * @return list of active subjects
     */
    List<Subject> findByActiveTrueOrderBySubjectNameAsc();
}