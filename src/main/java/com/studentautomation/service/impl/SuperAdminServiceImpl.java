package com.studentautomation.service.impl;

import com.studentautomation.dto.request.CreateAdminRequestDTO;
import com.studentautomation.dto.response.AdminResponseDTO;
import com.studentautomation.entity.User;
import com.studentautomation.enums.AccountStatus;
import com.studentautomation.enums.Role;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.repository.UserRepository;
import com.studentautomation.service.SuperAdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for Super Admin operations.
 *
 * Purpose:
 * This class contains business logic that only Super Admin can perform.
 *
 * @author Yashvanth
 */
@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminServiceImpl(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new Admin account.
     *
     * Purpose:
     * This method validates duplicate email, checks password confirmation,
     * encrypts password, assigns ADMIN role, and saves the admin user.
     *
     * @param request admin account creation request
     * @return created admin response details
     */
    @Override
    @Transactional
    public AdminResponseDTO createAdmin(CreateAdminRequestDTO request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new InvalidRequestException("Password and confirm password do not match");
        }

        User admin = new User();
        admin.setName(request.name());
        admin.setEmail(request.email());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole(Role.ADMIN);
        admin.setAccountStatus(AccountStatus.ACTIVE);

        User savedAdmin = userRepository.save(admin);

        return new AdminResponseDTO(
                savedAdmin.getId(),
                savedAdmin.getName(),
                savedAdmin.getEmail(),
                savedAdmin.getRole(),
                savedAdmin.getAccountStatus(),
                savedAdmin.getCreatedAt()
        );
    }
}