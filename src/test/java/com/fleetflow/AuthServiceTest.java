package com.fleetflow;

import com.fleetflow.dto.LoginRequest;
import com.fleetflow.dto.LoginResponse;
import com.fleetflow.dto.RegisterRequest;
import com.fleetflow.entity.Role;
import com.fleetflow.entity.User;
import com.fleetflow.exception.DuplicateResourceException;
import com.fleetflow.repository.UserRepository;
import com.fleetflow.security.JwtUtil;
import com.fleetflow.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthServiceTest — unit tests for AuthService.
 *
 * @ExtendWith(MockitoExtension.class)
 *   Tells JUnit to use Mockito for creating mocks.
 *
 * @Mock
 *   Creates a fake version of the dependency.
 *   We control exactly what it returns.
 *   No real database is used — everything is simulated.
 *
 * @InjectMocks
 *   Creates the real AuthService and injects all @Mock fields into it.
 *
 * WHY MOCK?
 *   Unit tests must be FAST and ISOLATED.
 *   We test only the AuthService logic — not the database,
 *   not the password encoder, not the JWT library.
 *   Mocks let us fake those and focus on what AuthService does.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User savedUser;

    // Runs before EACH test method
    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@fleetflow.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.DISPATCHER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@fleetflow.com");
        loginRequest.setPassword("password123");

        savedUser = User.builder()
                .id(1L)
                .email("test@fleetflow.com")
                .passwordHash("$2a$10$hashedpassword")
                .role(Role.DISPATCHER)
                .active(true)
                .build();
    }

    // ── Register Tests ────────────────────────────────────────────────────

    @Test
    void register_success_whenEmailNotTaken() {
        // ARRANGE — set up what the mocks should return
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // ACT — call the method we are testing
        String result = authService.register(registerRequest);

        // ASSERT — check the result is what we expected
        assertEquals("User registered successfully", result);

        // VERIFY — confirm save() was called exactly once
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_throwsDuplicateException_whenEmailAlreadyTaken() {
        // ARRANGE — simulate email already exists
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // ACT + ASSERT — expect this exception to be thrown
        assertThrows(DuplicateResourceException.class,
                () -> authService.register(registerRequest));

        // VERIFY — save() should NEVER be called if email is taken
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_passwordIsHashed_notStoredAsPlainText() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        authService.register(registerRequest);

        // Verify the password encoder was called with the plain text password
        verify(passwordEncoder, times(1)).encode("password123");
    }

    // ── Login Tests ───────────────────────────────────────────────────────

    @Test
    void login_success_returnsTokenAndUserInfo() {
        // ARRANGE
        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // authentication passes (no exception thrown)
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken(anyString()))
                .thenReturn("mock.jwt.token");

        // ACT
        LoginResponse response = authService.login(loginRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("test@fleetflow.com", response.getEmail());
        assertEquals(Role.DISPATCHER, response.getRole());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void login_callsAuthenticationManager() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken(anyString())).thenReturn("token");

        authService.login(loginRequest);

        // Verify authentication was checked
        verify(authenticationManager, times(1)).authenticate(any());
    }
}
