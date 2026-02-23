package com.citymate.userapi.service;

import com.citymate.userapi.dto.JwtResponse;
import com.citymate.userapi.dto.LoginRequest;
import com.citymate.userapi.dto.RegisterRequest;
import com.citymate.userapi.entity.Role;
import com.citymate.userapi.entity.User;
import com.citymate.userapi.exception.ConflictException;
import com.citymate.userapi.exception.ResourceNotFoundException;
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
        // Lève BadCredentialsException si incorrect
        // → Capturé par GlobalExceptionHandler → 401
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        return new JwtResponse(jwt, loginRequest.getUsername());
    }

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

        // Créer l'utilisateur
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setProfileType(registerRequest.getProfileType());
        user.setIsActive(true);
        user.setIsVerified(false);

        // Assigner le rôle CLIENT
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "CLIENT"));

        Set<Role> roles = new HashSet<>();
        roles.add(clientRole);
        user.setRoles(roles);

        userRepository.save(user);

        String jwt = jwtTokenProvider.generateTokenFromUsername(user.getUsername());
        return new JwtResponse(jwt, user.getUsername());
    }
}