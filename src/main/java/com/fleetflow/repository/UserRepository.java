package com.fleetflow.repository;

import com.fleetflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository — handles all database operations for User.
 *
 * By extending JpaRepository<User, Long> we get these methods
 * FOR FREE without writing any SQL:
 *
 *   save(user)          → INSERT or UPDATE a user row
 *   findById(id)        → SELECT * FROM users WHERE id = ?
 *   findAll()           → SELECT * FROM users
 *   deleteById(id)      → DELETE FROM users WHERE id = ?
 *   existsById(id)      → SELECT COUNT(*) FROM users WHERE id = ?
 *   count()             → SELECT COUNT(*) FROM users
 *
 * We only need to add methods that JpaRepository doesn't have.
 * Spring Data JPA reads the method NAME and writes the SQL for us.
 *
 * findByEmail(email)
 *   → SELECT * FROM users WHERE email = ?
 *   → Returns Optional<User> because the user might not exist
 *   → Used during login to find who is trying to log in
 *
 * existsByEmail(email)
 *   → SELECT COUNT(*) FROM users WHERE email = ?  (returns boolean)
 *   → Used during registration to check if email is already taken
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their email address
    // Returns Optional because user might not exist in DB
    Optional<User> findByEmail(String email);

    // Check if an email is already registered
    // Returns true if found, false if not
    boolean existsByEmail(String email);
}
