package com.citymate.userapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre qui intercepte TOUTES les requêtes HTTP
 * pour vérifier la présence et validité du token JWT
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Extraire le JWT depuis le header Authorization
            String jwt = getJwtFromRequest(request);

            // 2. Vérifier si le token est présent et valide
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {

                // 3. Vérifier que c'est bien un ACCESS TOKEN (pas un refresh token)
                if (!jwtUtil.isAccessToken(jwt)) {
                    logger.error("Token is not an access token");
                    filterChain.doFilter(request, response);
                    return;
                }

                // 4. Extraire le username depuis le token
                String username = jwtUtil.extractUsername(jwt);

                // 5. Charger les détails de l'utilisateur depuis la DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 6. Créer l'objet d'authentification Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 7. Configurer Spring Security avec cette authentification
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification JWT: " + e.getMessage());
        }

        // 8. Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT depuis le header Authorization
     * Format attendu : "Authorization: Bearer <token>"
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Vérifier le format "Bearer <token>"
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}