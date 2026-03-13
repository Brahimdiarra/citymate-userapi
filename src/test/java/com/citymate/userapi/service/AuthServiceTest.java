package com.citymate.userapi.service;

import com.citymate.userapi.dto.JwtResponse;
import com.citymate.userapi.dto.LoginRequest;
import com.citymate.userapi.dto.RegisterRequest;
import com.citymate.userapi.entity.Role;
import com.citymate.userapi.entity.User;
import com.citymate.userapi.exception.ConflictException;
import com.citymate.userapi.exception.UnauthorizedException;
import com.citymate.userapi.repository.RoleRepository;
import com.citymate.userapi.repository.UserRepository;
import com.citymate.userapi.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AuthService.
 * Les dépendances sont mockées — aucune base de données requise.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository        userRepository;
    @Mock private RoleRepository        roleRepository;
    @Mock private PasswordEncoder       passwordEncoder;
    @Mock private JwtUtil               jwtUtil;

    @InjectMocks
    private AuthService authService;

    // ─────────────────────────────────────────
    // login
    // ─────────────────────────────────────────

    @Test
    void login_withValidCredentials_returnsJwtResponse() {
        // Given
        LoginRequest req = new LoginRequest("alice", "Test1234!");

        Role role = new Role("STUDENT", "Étudiant");
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setRoles(Set.of(role));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("alice", "Test1234!"));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateAccessToken("alice", "STUDENT", 1L)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("alice")).thenReturn("refresh-token");

        // When
        JwtResponse response = authService.login(req);

        // Then
        assertNotNull(response);
        assertEquals("alice", response.getUsername());
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("STUDENT", response.getRole());
    }

    @Test
    void login_withBadCredentials_throwsBadCredentialsException() {
        // Given
        LoginRequest req = new LoginRequest("alice", "mauvais-mdp");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Identifiants invalides"));

        // Then
        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }

    // ─────────────────────────────────────────
    // register
    // ─────────────────────────────────────────

    @Test
    void register_withValidData_returnsJwtResponse() {
        // Given
        RegisterRequest req = new RegisterRequest(
                "bob", "bob@example.com", "Test1234!",
                "Bob", "Martin", User.ProfileType.STUDENT
        );

        Role studentRole = new Role("STUDENT", "Étudiant");

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername("bob");

        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(studentRole));
        when(passwordEncoder.encode("Test1234!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateAccessToken("bob", "STUDENT", 2L)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("bob")).thenReturn("refresh-token");

        // When
        JwtResponse response = authService.register(req);

        // Then
        assertNotNull(response);
        assertEquals("bob", response.getUsername());
        assertEquals("STUDENT", response.getRole());
        verify(passwordEncoder).encode("Test1234!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_withExistingUsername_throwsConflictException() {
        // Given
        RegisterRequest req = new RegisterRequest(
                "alice", "autre@example.com", "Test1234!",
                "Alice", "Doe", User.ProfileType.STUDENT
        );
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        // Then
        assertThrows(ConflictException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_withExistingEmail_throwsConflictException() {
        // Given
        RegisterRequest req = new RegisterRequest(
                "newuser", "alice@example.com", "Test1234!",
                "New", "User", User.ProfileType.STUDENT
        );
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        // Then
        assertThrows(ConflictException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    // ─────────────────────────────────────────
    // refresh
    // ─────────────────────────────────────────

    @Test
    void refresh_withValidRefreshToken_returnsNewAccessToken() {
        // Given
        String refreshToken = "valid-refresh-token";
        Role role = new Role("STUDENT", "Étudiant");
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setRoles(Set.of(role));

        when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken("alice", "STUDENT", 1L)).thenReturn("new-access-token");

        // When
        JwtResponse response = authService.refresh(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("alice", response.getUsername());
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
    }

    @Test
    void refresh_withAccessTokenInsteadOfRefresh_throwsUnauthorizedException() {
        // Given
        String accessToken = "access-token-not-refresh";
        when(jwtUtil.isRefreshToken(accessToken)).thenReturn(false);

        // Then
        assertThrows(UnauthorizedException.class, () -> authService.refresh(accessToken));
    }
}
