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
        String accessToken = jwtUtil.generateAccessToken(loginRequest.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getUsername());

        return new JwtResponse(accessToken, refreshToken, loginRequest.getUsername());
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


        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "CLIENT"));

        Set<Role> roles = new HashSet<>();
        roles.add(clientRole);
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
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return new JwtResponse(accessToken, refreshToken, user.getUsername());
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
        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Générer un nouvel access token
        String newAccessToken = jwtUtil.generateAccessToken(username);

        return new JwtResponse(newAccessToken, refreshToken, username);
    }
}