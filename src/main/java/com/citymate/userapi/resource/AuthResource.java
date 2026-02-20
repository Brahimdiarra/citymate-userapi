package com.citymate.userapi.resource;

import com.citymate.userapi.dto.JwtResponse;
import com.citymate.userapi.dto.LoginRequest;
import com.citymate.userapi.dto.RegisterRequest;
import com.citymate.userapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resource JAX-RS pour l'authentification
 * Endpoints:
 * - POST /api/v1/auth/login
 * - POST /api/v1/auth/register
 * - GET /api/v1/auth/health
 */
@Component
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Endpoints pour l'authentification et l'inscription")
public class AuthResource {

    @Autowired
    private AuthService authService;

    @POST
    @Path("/login")
    @Operation(summary = "Authentifier un utilisateur", description = "Valide les credentials et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification reussie", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credentials invalides")
    })
    public Response login(@Valid LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.login(loginRequest);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Erreur : Username ou mot de passe incorrect")
                    .build();
        }
    }

    @POST
    @Path("/register")
    @Operation(summary = "Creer un nouveau compte", description = "Enregistre un nouvel utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription reussie", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Donnees invalides ou utilisateur existant")
    })
    public Response register(@Valid RegisterRequest registerRequest) {
        try {
            JwtResponse response = authService.register(registerRequest);
            return Response.ok(response).build();
        } catch (RuntimeException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/health")
    @Operation(summary = "Health check", description = "Verifie si l'API est operationnelle")
    public Response health() {
        return Response.ok("USER API is running").build();
    }
}