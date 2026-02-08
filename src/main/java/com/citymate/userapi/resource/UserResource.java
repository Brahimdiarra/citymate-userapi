package com.citymate.userapi.resource;

import com.citymate.userapi.dto.UserDTO;
import com.citymate.userapi.entity.User;
import com.citymate.userapi.repository.UserRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resource JAX-RS pour les utilisateurs
 * Endpoints :
 * - GET /api/v1/users/me
 * - GET /api/v1/users/{username}
 */
@Component
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Autowired
    private UserRepository userRepository;

    /**
     * Récupère les infos de l'utilisateur connecté
     * GET /api/v1/users/me
     *
     * Nécessite authentification (JWT token)
     */
    @GET
    @Path("/me")
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        try {
            // Récupérer le username depuis le contexte de sécurité
            String username = securityContext.getUserPrincipal().getName();

            // Charger l'utilisateur depuis la DB
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            // Convertir en DTO (ne pas exposer le passwordHash)
            UserDTO userDTO = new UserDTO(user);

            return Response.ok(userDTO).build();
        } catch (NotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Utilisateur non trouvé")
                    .build();
        }
    }

    /**
     * Récupère les infos publiques d'un utilisateur
     * GET /api/v1/users/{username}
     *
     * Nécessite authentification (JWT token)
     */
    @GET
    @Path("/{username}")
    public Response getUserByUsername(@PathParam("username") String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            UserDTO userDTO = new UserDTO(user);

            return Response.ok(userDTO).build();
        } catch (NotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Utilisateur non trouvé")
                    .build();
        }
    }
}