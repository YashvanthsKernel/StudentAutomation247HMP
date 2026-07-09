package com.studentautomation.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity class for storing student academic and profile details.
 *
 * Purpose:
 * This class represents the students table in the database.
 * It stores student-specific details like register number,
 * department, semester, section, and academic year.
 *
 * Important:
 * Login details like email, password, role, and account status
 * are stored in User entity, not here.
 *
 * @author Yashvanth
 */
@Entity
@Table(
        name = "students",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_student_reg_no", columnNames = "reg_no"),
                @UniqueConstraint(name = "uk_student_user_id", columnNames = "user_id")
        }
)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Register number should be unique for every student.
     */
    @Column(name = "reg_no", nullable = false, unique = true, length = 50)
    private String regNo;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "phone_no", length = 15)
    private String phoneNo;

    @Column(nullable = false, length = 80)
    private String department;

    @Column(nullable = false)
    private Integer semester;

    @Column(length = 20)
    private String section;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(nullable = false)
    private Boolean active = true;

    /**
     * One student is linked with one user login account.
     *
     * Example:
     * Student profile → user_id → User login details
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Student() {
    }

    public Student(Long id, String regNo, String name, String phoneNo,
                   String department, Integer semester, String section,
                   String academicYear, Boolean active, User user,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.regNo = regNo;
        this.name = name;
        this.phoneNo = phoneNo;
        this.department = department;
        this.semester = semester;
        this.section = section;
        this.academicYear = academicYear;
        this.active = active;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * This method runs automatically before saving a new student.
     *
     * Purpose:
     * It stores the student creation time.
     */
    @PrePersist
    public void beforeSave() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * This method runs automatically before updating an existing student.
     *
     * Purpose:
     * It stores the latest student update time.
     */
    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}