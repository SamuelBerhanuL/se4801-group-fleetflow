package com.fleetflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.dto.LoginRequest;
import com.fleetflow.dto.LoginResponse;
import com.fleetflow.dto.RegisterRequest;
import com.fleetflow.entity.Role;
import com.fleetflow.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthControllerTest — tests the HTTP layer of AuthController.
 *
 * Changed from @WebMvcTest to @SpringBootTest + @AutoConfigureMockMvc
 * because our SecurityConfig has dependencies (JwtAuthFilter, JwtUtil)
 * that @WebMvcTest struggles to wire in Spring Boot 3.5.
 *
 * @SpringBootTest loads the full context (but we mock AuthService).
 * @AutoConfigureMockMvc gives us MockMvc without a real server.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    // ── Register Tests ────────────────────────────────────────────────────

    @Test
    void register_returns201_whenValidRequest() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn("User registered successfully");

        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@fleetflow.com");
        request.setPassword("password123");
        request.setRole(Role.DISPATCHER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void register_returns400_whenEmailIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("not-an-email");
        request.setPassword("password123");
        request.setRole(Role.DRIVER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_returns400_whenPasswordTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@fleetflow.com");
        request.setPassword("123");
        request.setRole(Role.DRIVER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── Login Tests ───────────────────────────────────────────────────────

    @Test
    void login_returns200_withToken_whenValidCredentials() throws Exception {
        LoginResponse mockResponse = new LoginResponse(
                "mock.jwt.token",
                "admin@fleetflow.com",
                Role.ADMIN,
                "Login successful"
        );
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@fleetflow.com");
        request.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.email").value("admin@fleetflow.com"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void login_returns401_whenWrongPassword() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@fleetflow.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_returns400_whenEmailMissing() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
