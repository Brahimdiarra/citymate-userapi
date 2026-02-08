package com.citymate.userapi.service;

import com.citymate.userapi.dto.JwtResponse;
import com.citymate.userapi.dto.LoginRequest;
import com.citymate.userapi.dto.RegisterRequest;
import com.citymate.userapi.entity.Role;
import com.citymate.userapi.entity.User;
import com.citymate.userapi.repository.RoleRepository;
import com.citymate.userapi.repository.UserRepository;
import com.citymate.userapi.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Connexion d'un utilisateur
     * @param loginRequest Username + Password
     * @return Token JWT
     */
    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        // 1. Authentifier l'utilisateur avec Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Stocker l'authentification dans le SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Générer le JWT token
        String jwt = jwtTokenProvider.generateToken(authentication);

        // 4. Retourner le token au client
        return new JwtResponse(jwt, loginRequest.getUsername());
    }

    /**
     * Inscription d'un nouvel utilisateur
     * @param registerRequest Données d'inscription
     * @return Token JWT
     */
    @Transactional
    public JwtResponse register(RegisterRequest registerRequest) {
        // 1. Vérifier si username existe déjà
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Erreur : Username déjà utilisé !");
        }

        // 2. Vérifier si email existe déjà
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Erreur : Email déjà utilisé !");
        }

        // 3. Créer le nouvel utilisateur
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setProfileType(registerRequest.getProfileType());
        user.setIsActive(true);
        user.setIsVerified(false);

        // 4. Assigner le rôle CLIENT par défaut
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Erreur : Rôle CLIENT non trouvé dans la base !"));

        Set<Role> roles = new HashSet<>();
        roles.add(clientRole);
        user.setRoles(roles);

        // 5. Sauvegarder l'utilisateur dans la DB
        userRepository.save(user);

        // 6. Générer un JWT token
        String jwt = jwtTokenProvider.generateTokenFromUsername(user.getUsername());

        // 7. Retourner le token
        return new JwtResponse(jwt, user.getUsername());
    }
}