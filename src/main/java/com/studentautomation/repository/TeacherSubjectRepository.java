package com.studentautomation.repository;

import com.studentautomation.entity.TeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TeacherSubject entity.
 *
 * Purpose:
 * Performs database operations for assignments between
 * teachers and academic subjects.
 *
 * Spring Data JPA automatically generates the implementation
 * for the methods declared in this repository.
 *
 * @author Yashvanth
 */
@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long> {

    /**
     * Finds an existing teacher-subject assignment using the complete
     * assignment combination.
     *
     * This method checks both active and inactive assignments.
     * If an inactive assignment already exists, we can reactivate it
     * instead of creating a duplicate database row.
     *
     * @param teacherId ID of the assigned teacher
     * @param subjectId ID of the assigned subject
     * @param section class section
     * @param academicYear academic year
     * @return matching assignment when found
     */
    Optional<TeacherSubject> findByTeacher_IdAndSubject_IdAndSectionIgnoreCaseAndAcademicYearIgnoreCase(
            Long teacherId,
            Long subjectId,
            String section,
            String academicYear
    );

    /**
     * Checks whether the exact teacher-subject assignment already exists.
     *
     * This is used to prevent duplicate assignments.
     *
     * Example:
     * Teacher 5 + Subject 1 + Section A + 2026-2027
     *
     * @param teacherId ID of the teacher
     * @param subjectId ID of the subject
     * @param section class section
     * @param academicYear academic year
     * @return true when the assignment already exists
     */
    boolean existsByTeacher_IdAndSubject_IdAndSectionIgnoreCaseAndAcademicYearIgnoreCase(
            Long teacherId,
            Long subjectId,
            String section,
            String academicYear
    );

    /**
     * Retrieves all currently active teacher-subject assignments.
     *
     * The latest assignments are returned first.
     *
     * @return list of active teacher-subject assignments
     */
    List<TeacherSubject> findByActiveTrueOrderByCreatedAtDesc();

    /**
     * Retrieves all active subject assignments belonging to a teacher.
     *
     * Results are sorted alphabetically using the subject name.
     *
     * @param teacherId ID of the teacher
     * @return active assignments belonging to the teacher
     */
    List<TeacherSubject> findByTeacher_IdAndActiveTrueOrderBySubject_SubjectNameAsc(
            Long teacherId
    );

    /**
     * Retrieves all active teacher assignments belonging to a subject.
     *
     * Results are sorted alphabetically using the teacher name.
     *
     * @param subjectId ID of the subject
     * @return active assignments belonging to the subject
     */
    List<TeacherSubject> findBySubject_IdAndActiveTrueOrderByTeacher_NameAsc(
            Long subjectId
    );

    /**
     * Checks whether a teacher has an active assignment for
     * a subject, section, and academic year.
     *
     * @param teacherId teacher ID
     * @param subjectId subject ID
     * @param section assigned section
     * @param academicYear assigned academic year
     * @return true when an active assignment exists
     */
    boolean existsByTeacher_IdAndSubject_IdAndSectionIgnoreCaseAndAcademicYearAndActiveTrue(
            Long teacherId,
            Long subjectId,
            String section,
            String academicYear
    );
}