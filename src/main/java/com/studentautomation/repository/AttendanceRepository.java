package com.studentautomation.repository;

import com.studentautomation.entity.Attendance;
import com.studentautomation.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Attendance entity.
 *
 * Purpose:
 * This interface handles database operations related to student attendance.
 *
 * @author Yashvanth
 */
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByStudentIdAndAttendanceDateAndSubjectNameAndPeriodNumber(
            Long studentId,
            LocalDate attendanceDate,
            String subjectName,
            Integer periodNumber
    );

    Optional<Attendance> findByStudentIdAndAttendanceDateAndSubjectNameAndPeriodNumber(
            Long studentId,
            LocalDate attendanceDate,
            String subjectName,
            Integer periodNumber
    );

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByMarkedByIdAndAttendanceDate(Long teacherId, LocalDate attendanceDate);

}