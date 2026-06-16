package com.fleetflow.security;

import com.fleetflow.entity.User;
import com.fleetflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserDetailsServiceImpl — Spring Security calls this during login
 * to load a user from the database by their email.
 *
 * WHY IT EXISTS:
 * Spring Security doesn't know about our User entity or PostgreSQL.
 * It only knows about its own UserDetails interface.
 * This class is the BRIDGE between our database and Spring Security.
 *
 * HOW IT WORKS:
 * 1. User sends POST /api/auth/login with email + password
 * 2. Spring Security calls loadUserByUsername(email)
 * 3. We fetch the User from our database by email
 * 4. We return a UserDetails object Spring Security understands
 * 5. Spring Security then checks the password automatically
 *
 * @RequiredArgsConstructor = Lombok generates constructor
 *   with all final fields injected (userRepository here)
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Find user in database by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        // Convert our Role enum to Spring Security's GrantedAuthority
        // Spring Security expects roles prefixed with "ROLE_"
        // So ADMIN becomes ROLE_ADMIN, DRIVER becomes ROLE_DRIVER etc.
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        // Return Spring Security's User object (not our User entity)
        // Parameters: username, password, authorities
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }
}
