package com.studentautomation.entity;

import com.studentautomation.enums.AccountStatus;
import com.studentautomation.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity class for storing user login and security details.
 *
 * Purpose:
 * This class represents the users table in the database.
 * It stores only authentication-related details like email,
 * password, role, and account status.
 *
 * Important:
 * Student and Teacher details should not be stored directly here.
 *
 * @author Yashvanth
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = "email")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email is used as the login username.
     */
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /**
     * Password will be stored in encrypted/hashed format later using BCrypt.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Role tells what type of user this is.
     *
     * Example:
     * STUDENT, TEACHER, ADMIN, SUPER_ADMIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    /**
     * Account status tells whether this user can login or not.
     *
     * Example:
     * ACTIVE, INACTIVE, BLOCKED, DELETED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Full name of the user.
     *
     * Purpose:
     * This is used for Admin and Super Admin accounts.
     * Student and Teacher profile details will be stored in their own tables.
     */
    @Column(length = 100)
    private String name;

    public User() {
    }


    //parameterized constructor
    public User(Long id, String email, String password, Role role,
                AccountStatus accountStatus, LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * This method runs automatically before saving a new user.
     *
     * Purpose:
     * It stores the user creation time.
     */
    @PrePersist
    public void beforeSave() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * This method runs automatically before updating an existing user.
     *
     * Purpose:
     * It stores the latest user update time.
     */
    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}