package com.studentautomation.repository;

import com.studentautomation.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByExamCodeIgnoreCase(String examCode);
    boolean existsByExamCodeIgnoreCase(String examCode);
}
