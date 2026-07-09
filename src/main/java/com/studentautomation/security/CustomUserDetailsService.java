package com.studentautomation.security;

import com.studentautomation.entity.User;
import com.studentautomation.enums.AccountStatus;
import com.studentautomation.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class for loading user details for Spring Security.
 *
 * Purpose:
 * This class loads user login information from the database using email.
 * Spring Security uses this class while validating JWT authentication.
 *
 * @author Yashvanth
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details by email.
     *
     * Purpose:
     * Spring Security calls this method to find a user from database.
     * In our project, email is treated as username.
     *
     * @param email user email address
     * @return UserDetails object required by Spring Security
     * @throws UsernameNotFoundException if user is not found or inactive
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new UsernameNotFoundException("User account is not active");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}