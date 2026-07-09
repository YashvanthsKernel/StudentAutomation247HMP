package com.studentautomation.repository;

import com.studentautomation.entity.User;
import com.studentautomation.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 *
 * Purpose:
 * This interface is used to perform database operations
 * related to user login and security details.
 *
 * @author Yashvanth
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     *
     * Purpose:
     * This is mainly used during login and registration
     * to check whether an email already exists.
     *
     * @param email user email address
     * @return user details if email exists
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user already exists with the given email.
     *
     * Purpose:
     * This helps prevent duplicate user registration.
     *
     * @param email user email address
     * @return true if email exists, otherwise false
     */
    boolean existsByEmail(String email);

    /**
     * Finds all users by role.
     *
     * Purpose:
     * This can be used by admin to filter users by role.
     *
     * @param role user role
     * @return list of users with the given role
     */
    List<User> findByRole(Role role);

    /**
     * Checks whether at least one user exists with the given role.
     *
     * Purpose:
     * This is mainly used during application startup to check
     * whether a default SUPER_ADMIN account already exists.
     *
     * @param role user role to check
     * @return true if user exists with the given role, otherwise false
     */
    boolean existsByRole(Role role);
}