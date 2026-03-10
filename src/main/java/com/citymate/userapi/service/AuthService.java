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

/**
 * Service qui gère l'authentification
 * - Login
 * - Register
 */
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

    /**
     * Connexion d'un utilisateur
     * @param loginRequest Username + Password
     * @return Token JWT
     */
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

        String accessToken = jwtUtil.generateAccessToken(loginRequest.getUsername(), role);
        String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getUsername());

        JwtResponse response = new JwtResponse(accessToken, refreshToken, loginRequest.getUsername());
        response.setRole(role);
        return response;
    }

    /**
     * Inscription d'un nouvel utilisateur
     * @param registerRequest Données d'inscription
     * @return Token JWT
     */
    @Transactional
    public JwtResponse register(RegisterRequest registerRequest) {
        // Vérifier si username existe
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ConflictException("Username déjà utilisé");
        }

        // Vérifier si email existe
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
            userRepository.save(user);
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

        // Générer tokens
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), "STUDENT");
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        JwtResponse response = new JwtResponse(accessToken, refreshToken, user.getUsername());
        response.setRole("STUDENT");
        return response;
    }

    /**
     * Renouvellement d'un access token via un refresh token valide
     * @param refreshToken Le refresh token (24h) envoyé par le client
     * @return Nouveau JwtResponse avec un nouvel access token
     */
    public JwtResponse refresh(String refreshToken) {
        // Vérifier que c'est bien un refresh token (type = "refresh")
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Token fourni n'est pas un refresh token");
        }

        // Vérifier que le refresh token est valide et non expiré
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException("Refresh token invalide ou expiré");
        }

        // Extraire le username du refresh token
        String username = jwtUtil.extractUsername(refreshToken);

        // Vérifier que l'utilisateur existe toujours en base
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        String role = existingUser.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("CLIENT");

        // Générer un nouvel access token avec le rôle
        String newAccessToken = jwtUtil.generateAccessToken(username, role);

        JwtResponse response = new JwtResponse(newAccessToken, refreshToken, username);
        response.setRole(role);
        return response;
    }
}