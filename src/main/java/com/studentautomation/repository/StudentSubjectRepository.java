package com.studentautomation.repository;

import com.studentautomation.entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StudentSubject entity.
 *
 * Purpose:
 * Performs database operations for assignments between
 * students and academic subjects.
 *
 * Spring Data JPA automatically generates the implementation
 * for the query methods declared in this interface.
 *
 * @author Yashvanth
 */
@Repository
public interface StudentSubjectRepository
        extends JpaRepository<StudentSubject, Long> {

    /**
     * Finds a student-subject assignment using the complete
     * assignment combination.
     *
     * This method checks both active and inactive assignments.
     * An inactive assignment can later be reactivated instead
     * of creating another database row.
     *
     * @param studentId database ID of the student
     * @param subjectId database ID of the subject
     * @param academicYear academic year of the assignment
     * @return matching assignment when found
     */
    Optional<StudentSubject>
    findByStudent_IdAndSubject_IdAndAcademicYearIgnoreCase(
            Long studentId,
            Long subjectId,
            String academicYear
    );

    /**
     * Checks whether the exact student-subject assignment exists.
     *
     * This method is used to prevent duplicate assignments.
     *
     * @param studentId database ID of the student
     * @param subjectId database ID of the subject
     * @param academicYear academic year of the assignment
     * @return true when the assignment already exists
     */
    boolean existsByStudent_IdAndSubject_IdAndAcademicYearIgnoreCase(
            Long studentId,
            Long subjectId,
            String academicYear
    );

    /**
     * Checks whether a student has an active assignment
     * for a subject during a particular academic year.
     *
     * This method will later be used while validating
     * attendance and marks operations.
     *
     * @param studentId database ID of the student
     * @param subjectId database ID of the subject
     * @param academicYear academic year
     * @return true when an active assignment exists
     */
    boolean
    existsByStudent_IdAndSubject_IdAndAcademicYearIgnoreCaseAndActiveTrue(
            Long studentId,
            Long subjectId,
            String academicYear
    );

    /**
     * Retrieves all currently active student-subject assignments.
     *
     * The latest assignments are returned first.
     *
     * @return list of active student-subject assignments
     */
    List<StudentSubject> findByActiveTrueOrderByCreatedAtDesc();

    /**
     * Retrieves all active subject assignments belonging
     * to a particular student.
     *
     * Results are sorted alphabetically by subject name.
     *
     * @param studentId database ID of the student
     * @return active subject assignments belonging to the student
     */
    List<StudentSubject>
    findByStudent_IdAndActiveTrueOrderBySubject_SubjectNameAsc(
            Long studentId
    );

    /**
     * Retrieves all active student assignments belonging
     * to a particular subject.
     *
     * Results are sorted alphabetically by student name.
     *
     * @param subjectId database ID of the subject
     * @return active student assignments belonging to the subject
     */
    List<StudentSubject>
    findBySubject_IdAndActiveTrueOrderByStudent_NameAsc(
            Long subjectId
    );
}