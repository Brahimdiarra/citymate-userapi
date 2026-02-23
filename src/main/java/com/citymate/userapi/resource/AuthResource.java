package com.citymate.userapi.resource;

import com.citymate.userapi.dto.ErrorResponse;
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
    @Operation(summary = "Authentifier un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie"),
            @ApiResponse(responseCode = "401", description = "Credentials invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response login(@Valid LoginRequest loginRequest) {
        // Pas de try-catch ! Les exceptions sont gérées par GlobalExceptionHandler
        JwtResponse response = authService.login(loginRequest);
        return Response.ok(response).build();
    }

    @POST
    @Path("/register")
    @Operation(summary = "Créer un nouveau compte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription réussie"),
            @ApiResponse(responseCode = "409", description = "Username ou email déjà utilisé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response register(@Valid RegisterRequest registerRequest) {
        // Pas de try-catch !
        JwtResponse response = authService.register(registerRequest);
        return Response.ok(response).build();
    }
}