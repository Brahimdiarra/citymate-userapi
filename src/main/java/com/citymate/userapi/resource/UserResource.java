package com.citymate.userapi.resource;

import com.citymate.userapi.dto.ErrorResponse;
import com.citymate.userapi.dto.PublicUserDTO;
import com.citymate.userapi.dto.UpdateProfileRequest;
import com.citymate.userapi.dto.UserDTO;
import com.citymate.userapi.exception.UnauthorizedException;
import com.citymate.userapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "Endpoints pour gérer les profils utilisateurs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserResource {

    @Autowired
    private UserService userService;

    @GET
    @Path("/me")
    @Operation(summary = "Récupérer mon profil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        if (securityContext.getUserPrincipal() == null) {
            throw new UnauthorizedException("Utilisateur non authentifié");
        }
        String username = securityContext.getUserPrincipal().getName();
        UserDTO userDTO = userService.getUserByUsername(username);
        return Response.ok(userDTO).build();
    }

    @GET
    @Path("/{username}")
    @Operation(summary = "Récupérer le profil public d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil public trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response getUserByUsername(
            @Context SecurityContext securityContext,
            @PathParam("username") String username) {

        if (securityContext.getUserPrincipal() == null) {
            throw new UnauthorizedException("Utilisateur non authentifié");
        }

        PublicUserDTO publicUserDTO = userService.getPublicUserByUsername(username);
        return Response.ok(publicUserDTO).build();
    }

    @PUT
    @Path("/me")
    @Operation(summary = "Mettre à jour mon profil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil mis à jour"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public Response updateProfile(
            @Context SecurityContext securityContext,
            @Valid UpdateProfileRequest request) {

        if (securityContext.getUserPrincipal() == null) {
            throw new UnauthorizedException("Utilisateur non authentifié");
        }
        String username = securityContext.getUserPrincipal().getName();
        UserDTO userDTO = userService.updateProfile(username, request);
        return Response.ok(userDTO).build();
    }
}
