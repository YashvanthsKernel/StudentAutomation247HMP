package com.studentautomation.repository;

import com.studentautomation.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Attendance entity.
 *
 * Purpose:
 * This interface handles database operations related to
 * student attendance records.
 *
 * @author Yashvanth
 */
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * Checks whether attendance already exists for the same student,
     * subject, attendance date, and period.
     *
     * @param studentId ID of the student
     * @param subjectId ID of the subject
     * @param attendanceDate attendance date
     * @param periodNumber class period number
     * @return true when matching attendance already exists
     */
    boolean existsByStudent_IdAndSubject_IdAndAttendanceDateAndPeriodNumber(
            Long studentId,
            Long subjectId,
            LocalDate attendanceDate,
            Integer periodNumber
    );

    /**
     * Finds one exact attendance record.
     *
     * @param studentId ID of the student
     * @param subjectId ID of the subject
     * @param attendanceDate attendance date
     * @param periodNumber class period number
     * @return matching attendance when found
     */
    Optional<Attendance> findByStudent_IdAndSubject_IdAndAttendanceDateAndPeriodNumber(
            Long studentId,
            Long subjectId,
            LocalDate attendanceDate,
            Integer periodNumber
    );

    /**
     * Finds all attendance records belonging to a student.
     *
     * @param studentId ID of the student
     * @return student's attendance records
     */
    List<Attendance> findByStudent_Id(Long studentId);

    /**
     * Finds attendance records marked by a teacher on a specific date.
     *
     * @param teacherId ID of the teacher
     * @param attendanceDate attendance date
     * @return attendance records marked by the teacher
     */
    List<Attendance> findByMarkedBy_IdAndAttendanceDate(
            Long teacherId,
            LocalDate attendanceDate
    );
}