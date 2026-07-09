package com.studentautomation.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity class for storing teacher profile and professional details.
 *
 * Purpose:
 * This class represents the teachers table in the database.
 * It stores teacher-specific details like employee ID,
 * department, designation, qualification, and experience.
 *
 * Important:
 * Login details like email, password, role, and account status
 * are stored in User entity, not here.
 *
 * @author Yashvanth
 */
@Entity
@Table(
        name = "teachers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_teacher_employee_id", columnNames = "employee_id"),
                @UniqueConstraint(name = "uk_teacher_user_id", columnNames = "user_id")
        }
)
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Employee ID should be unique for every teacher.
     */
    @Column(name = "employee_id", nullable = false, unique = true, length = 50)
    private String employeeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "phone_no", length = 15)
    private String phoneNo;

    @Column(nullable = false, length = 80)
    private String department;

    @Column(length = 80)
    private String designation;

    @Column(length = 120)
    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(nullable = false)
    private Boolean active = true;

    /**
     * One teacher is linked with one user login account.
     *
     * Example:
     * Teacher profile → user_id → User login details
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Teacher() {
    }

    /**
     * This method runs automatically before saving a new teacher.
     *
     * Purpose:
     * It stores the teacher creation time.
     */
    @PrePersist
    public void beforeSave() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * This method runs automatically before updating an existing teacher.
     *
     * Purpose:
     * It stores the latest teacher update time.
     */
    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
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