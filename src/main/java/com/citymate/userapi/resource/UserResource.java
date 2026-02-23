package com.citymate.userapi.resource;

import com.citymate.userapi.dto.ErrorResponse;
import com.citymate.userapi.dto.UserDTO;
import com.citymate.userapi.entity.User;
import com.citymate.userapi.exception.ResourceNotFoundException;
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
    @Operation(summary = "Récupérer mon profil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response getCurrentUser(@Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return Response.ok(new UserDTO(user)).build();
    }

    @GET
    @Path("/{username}")
    @Operation(summary = "Récupérer le profil d'un utilisateur")
    public Response getUserByUsername(@PathParam("username") String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return Response.ok(new UserDTO(user)).build();
    }
}