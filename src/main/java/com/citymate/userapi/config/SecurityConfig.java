package com.citymate.userapi.config;

import com.citymate.userapi.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de la sécurité Spring Security
 * Gère l'authentification JWT et les autorisations
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Bean pour encoder les mots de passe avec BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provider d'authentification
     * Connecte UserDetailsService + PasswordEncoder
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication Manager
     * Nécessaire pour le login
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configuration principale de la sécurité
     * Définit quels endpoints sont publics et lesquels nécessitent une authentification
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver CSRF (pas nécessaire pour une API REST)
                .csrf(csrf -> csrf.disable())

                // Configuration des autorisations
                .authorizeHttpRequests(auth -> auth
                        //  Endpoints PUBLICS (sans authentification)
                        .requestMatchers("/api/auth/**").permitAll()           // Login, Register
                        .requestMatchers("/actuator/**").permitAll()            // Health check
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll() // Swagger

                        // Endpoints ADMIN uniquement
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        //  Tous les autres endpoints nécessitent une authentification
                        .anyRequest().authenticated()
                )

                // Pas de sessions (stateless = JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configurer le provider d'authentification
                .authenticationProvider(authenticationProvider())

                // Ajouter notre filtre JWT AVANT le filtre d'authentification Spring
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}