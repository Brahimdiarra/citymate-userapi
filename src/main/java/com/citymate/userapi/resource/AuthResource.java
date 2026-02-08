package com.citymate.userapi.resource;

import com.citymate.userapi.dto.JwtResponse;
import com.citymate.userapi.dto.LoginRequest;
import com.citymate.userapi.dto.RegisterRequest;
import com.citymate.userapi.service.AuthService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resource JAX-RS pour l'authentification
 * Endpoints :
 * - POST /api/v1/auth/login
 * - POST /api/v1/auth/register
 */
@Component
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint de connexion
     * POST /api/v1/auth/login
     *
     * Body : { "username": "alice", "password": "password123" }
     *
     * @param loginRequest Username et password
     * @return Token JWT si succès
     */
    @POST
    @Path("/login")
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

    /**
     * Endpoint d'inscription
     * POST /api/v1/auth/register
     *
     * Body : {
     *   "username": "alice",
     *   "email": "alice@test.com",
     *   "password": "password123",
     *   "firstName": "Alice",
     *   "lastName": "Doe",
     *   "profileType": "STUDENT"
     * }
     *
     * @param registerRequest Données d'inscription
     * @return Token JWT si succès
     */
    @POST
    @Path("/register")
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

    /**
     * Endpoint de test (health check)
     * GET /api/auth/health
     */
    @GET
    @Path("/health")
    public Response health() {
        return Response.ok("USER API is running ✅").build();
    }
}