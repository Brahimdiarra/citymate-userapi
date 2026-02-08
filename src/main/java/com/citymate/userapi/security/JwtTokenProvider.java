package com.citymate.userapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Classe qui gère la création et validation des tokens JWT
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Génère un token JWT après authentification réussie
     * @param authentication Objet d'authentification Spring Security
     * @return Token JWT sous forme de String
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userDetails.getUsername());
    }

    /**
     * Génère un token JWT depuis un username
     * Utile pour l'inscription (register)
     * @param username Username de l'utilisateur
     * @return Token JWT
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(username)              // Le username dans le token
                .issuedAt(now)                  // Date de création
                .expiration(expiryDate)         // Date d'expiration
                .signWith(key)                  // Signature avec la clé secrète
                .compact();
    }

    /**
     * Extrait le username depuis le token JWT
     * @param token Token JWT
     * @return Username de l'utilisateur
     */
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Valide si un token JWT est valide
     * @param token Token JWT à valider
     * @return true si valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SecurityException e) {
            System.err.println("Signature JWT invalide: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token JWT mal formé: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Token JWT expiré: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token JWT non supporté: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims vide: " + e.getMessage());
        }
        return false;
    }
}