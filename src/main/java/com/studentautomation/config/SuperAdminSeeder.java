package com.studentautomation.config;

import com.studentautomation.entity.User;
import com.studentautomation.enums.AccountStatus;
import com.studentautomation.enums.Role;
import com.studentautomation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeder class for creating the first Super Admin account.
 *
 * Purpose:
 * In a real system, students and teachers cannot register themselves.
 * Admin creates students and teachers.
 * Super Admin creates admins.
 *
 * But the first Super Admin must exist before anyone can manage the system.
 * This class automatically creates one default Super Admin when the application starts.
 *
 * Important:
 * This class will not create duplicate Super Admin accounts.
 *
 * @author Yashvanth
 */
@Component
public class SuperAdminSeeder implements CommandLineRunner {

    private static final String DEFAULT_SUPER_ADMIN_EMAIL = "superadmin@gmail.com";
    private static final String DEFAULT_SUPER_ADMIN_PASSWORD = "SuperAdmin@123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminSeeder(UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Runs automatically when the Spring Boot application starts.
     *
     * Purpose:
     * This method checks whether a SUPER_ADMIN already exists.
     * If no SUPER_ADMIN exists, it creates one default Super Admin account.
     *
     * @param args command line arguments passed during application startup
     */
    @Override
    public void run(String... args) {

        if (userRepository.existsByRole(Role.SUPER_ADMIN)) {
            System.out.println("Super Admin already exists. Seeder skipped.");
            return;
        }

        if (userRepository.existsByEmail(DEFAULT_SUPER_ADMIN_EMAIL)) {
            throw new RuntimeException(
                    "Default Super Admin email already exists with another role. " +
                            "Please delete/change that user before seeding Super Admin."
            );
        }

        User superAdmin = new User();
        superAdmin.setEmail(DEFAULT_SUPER_ADMIN_EMAIL);
        superAdmin.setPassword(passwordEncoder.encode(DEFAULT_SUPER_ADMIN_PASSWORD));
        superAdmin.setRole(Role.SUPER_ADMIN);
        superAdmin.setAccountStatus(AccountStatus.ACTIVE);

        userRepository.save(superAdmin);

        System.out.println("Default Super Admin created successfully.");
        System.out.println("Email: " + DEFAULT_SUPER_ADMIN_EMAIL);
        System.out.println("Password: " + DEFAULT_SUPER_ADMIN_PASSWORD);
    }
}