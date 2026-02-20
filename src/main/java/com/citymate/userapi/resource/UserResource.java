package com.citymate.userapi.resource;

import com.citymate.userapi.dto.UserDTO;
import com.citymate.userapi.entity.User;
import com.citymate.userapi.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resource JAX-RS pour les utilisateurs
 * Endpoints:
 * - GET /api/v1/users/me
 * - GET /api/v1/users/{username}
 */
@Component
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "Endpoints pour gerer les profils utilisateurs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserResource {

    @Autowired
    private UserRepository userRepository;

    @GET
    @Path("/me")
    @Operation(summary = "Recuperer mon profil", description = "Retourne les informations du profil de l'utilisateur authentifie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil trouve", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouve")
    })
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            UserDTO userDTO = new UserDTO(user);
            return Response.ok(userDTO).build();
        } catch (NotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Utilisateur non trouve")
                    .build();
        }
    }

    @GET
    @Path("/{username}")
    @Operation(summary = "Recuperer le profil d'un utilisateur", description = "Retourne les informations publiques d'un utilisateur par son username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil trouve", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouve")
    })
    public Response getUserByUsername(@PathParam("username") String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            UserDTO userDTO = new UserDTO(user);
            return Response.ok(userDTO).build();
        } catch (NotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Utilisateur non trouve")
                    .build();
        }
    }
}