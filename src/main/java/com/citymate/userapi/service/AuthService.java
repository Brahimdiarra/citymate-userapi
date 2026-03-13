package com.citymate.userapi.service;

import com.citymate.userapi.dto.JwtResponse;
import com.citymate.userapi.dto.LoginRequest;
import com.citymate.userapi.dto.RegisterRequest;
import com.citymate.userapi.entity.Role;
import com.citymate.userapi.entity.User;
import com.citymate.userapi.exception.ConflictException;
import com.citymate.userapi.exception.ResourceNotFoundException;
import com.citymate.userapi.exception.UnauthorizedException;
import com.citymate.userapi.mapper.UserMapper;
import com.citymate.userapi.repository.RoleRepository;
import com.citymate.userapi.repository.UserRepository;
import com.citymate.userapi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur introuvable"));
        String role = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("CLIENT");

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(loginRequest.getUsername(), role, user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getUsername());

        JwtResponse response = new JwtResponse(accessToken, refreshToken, loginRequest.getUsername());
        response.setRole(role);
        return response;
    }

    @Transactional
    public JwtResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ConflictException("Username déjà utilisé");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConflictException("Email déjà utilisé");
        }


        User user = UserMapper.toEntity(registerRequest);


        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));


        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "STUDENT"));

        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        user.setRoles(roles);

        // Sauvegarder — le try/catch gère la race condition :
        // deux requêtes simultanées peuvent passer les checks ci-dessus
        // mais la contrainte UNIQUE PostgreSQL rejettera la seconde insertion
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Déterminer quel champ est en doublon via le message de l'exception
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("email")) {
                throw new ConflictException("Email déjà utilisé");
            } else if (msg.contains("username")) {
                throw new ConflictException("Username déjà utilisé");
            }
            throw new ConflictException("Username ou email déjà utilisé");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), "STUDENT", user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        JwtResponse response = new JwtResponse(accessToken, refreshToken, user.getUsername());
        response.setRole("STUDENT");
        return response;
    }

    public JwtResponse refresh(String refreshToken) {
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Token fourni n'est pas un refresh token");
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException("Refresh token invalide ou expiré");
        }

        String username = jwtUtil.extractUsername(refreshToken);

        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        String role = existingUser.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("CLIENT");

        String newAccessToken = jwtUtil.generateAccessToken(username, role, existingUser.getId());

        JwtResponse response = new JwtResponse(newAccessToken, refreshToken, username);
        response.setRole(role);
        return response;
    }
}