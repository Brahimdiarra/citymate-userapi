package com.citymate.userapi.service;

import com.citymate.userapi.dto.PublicUserDTO;
import com.citymate.userapi.dto.UpdateProfileRequest;
import com.citymate.userapi.dto.UserDTO;
import com.citymate.userapi.entity.User;
import com.citymate.userapi.exception.ResourceNotFoundException;
import com.citymate.userapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserService.
 * Les dépendances sont mockées — aucune base de données requise.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ─────────────────────────────────────────
    // getUserByUsername
    // ─────────────────────────────────────────

    @Test
    void getUserByUsername_withExistingUser_returnsUserDTO() {
        // Given
        User user = buildUser("alice", "alice@example.com");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        // When
        UserDTO result = userService.getUserByUsername("alice");

        // Then
        assertNotNull(result);
        assertEquals("alice", result.getUsername());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void getUserByUsername_withNonExistingUser_throwsResourceNotFoundException() {
        // Given
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Then
        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByUsername("unknown"));
    }

    // ─────────────────────────────────────────
    // getPublicUserByUsername
    // ─────────────────────────────────────────

    @Test
    void getPublicUserByUsername_withExistingUser_returnsPublicUserDTO() {
        // Given
        User user = buildUser("bob", "bob@example.com");
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        // When
        PublicUserDTO result = userService.getPublicUserByUsername("bob");

        // Then
        assertNotNull(result);
        assertEquals("bob", result.getUsername());
    }

    // ─────────────────────────────────────────
    // updateProfile
    // ─────────────────────────────────────────

    @Test
    void updateProfile_withValidData_updatesFieldsAndReturnsDTO() {
        // Given
        User user = buildUser("alice", "alice@example.com");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFirstName("Alicia");
        req.setCity("Brest");
        req.setBio("Étudiante à l'UBO");

        // When
        UserDTO result = userService.updateProfile("alice", req);

        // Then
        assertNotNull(result);
        assertEquals("Alicia", user.getFirstName());
        assertEquals("Brest", user.getCity());
        assertEquals("Étudiante à l'UBO", user.getBio());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_withNonExistingUser_throwsResourceNotFoundException() {
        // Given
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        // Then
        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateProfile("ghost", new UpdateProfileRequest()));
    }

    // ─────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────

    private User buildUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName("Prénom");
        user.setLastName("Nom");
        user.setIsActive(true);
        user.setIsVerified(false);
        return user;
    }
}
