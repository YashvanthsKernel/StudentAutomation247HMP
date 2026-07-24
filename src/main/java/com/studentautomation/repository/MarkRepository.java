package com.studentautomation.repository;

import com.studentautomation.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByStudent_IdAndSubject_IdAndExam_Id(Long studentId, Long subjectId, Long examId);
    List<Mark> findByStudent_Id(Long studentId);
    List<Mark> findByStudent_IdAndExam_Id(Long studentId, Long examId);
    List<Mark> findByStudent_IdAndSubject_Id(Long studentId, Long subjectId);
    List<Mark> findByExam_IdAndSubject_Id(Long examId, Long subjectId);
    List<Mark> findByExam_IdAndSubject_IdAndSection(Long examId, Long subjectId, String section);
    List<Mark> findBySubject_Id(Long subjectId);
    List<Mark> findByIsPassFalse();
    List<Mark> findTop10ByOrderByMarksObtainedDesc();
}
