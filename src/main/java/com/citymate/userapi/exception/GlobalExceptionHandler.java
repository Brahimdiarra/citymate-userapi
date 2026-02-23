package com.citymate.userapi.exception;

import com.citymate.userapi.dto.ErrorResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Gestionnaire global des exceptions pour JAX-RS
 * Intercepte toutes les exceptions et retourne des ErrorResponse standardisées
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {

        ErrorResponse errorResponse;
        Response.Status status;

        // ResourceNotFoundException → 404
        if (exception instanceof ResourceNotFoundException) {
            status = Response.Status.NOT_FOUND;
            errorResponse = new ErrorResponse(
                    status.getStatusCode(),
                    "Not Found",
                    exception.getMessage(),
                    getPath()
            );
        }
        // ConflictException → 409
        else if (exception instanceof ConflictException) {
            status = Response.Status.CONFLICT;
            errorResponse = new ErrorResponse(
                    status.getStatusCode(),
                    "Conflict",
                    exception.getMessage(),
                    getPath()
            );
        }
        // BadRequestException → 400
        else if (exception instanceof BadRequestException) {
            status = Response.Status.BAD_REQUEST;
            errorResponse = new ErrorResponse(
                    status.getStatusCode(),
                    "Bad Request",
                    exception.getMessage(),
                    getPath()
            );
        }
        // UnauthorizedException ou BadCredentialsException → 401
        else if (exception instanceof UnauthorizedException ||
                exception instanceof BadCredentialsException) {
            status = Response.Status.UNAUTHORIZED;
            errorResponse = new ErrorResponse(
                    status.getStatusCode(),
                    "Unauthorized",
                    "Username ou mot de passe incorrect",
                    getPath()
            );
        }
        // Exception générique → 500
        else {
            status = Response.Status.INTERNAL_SERVER_ERROR;
            errorResponse = new ErrorResponse(
                    status.getStatusCode(),
                    "Internal Server Error",
                    "Une erreur interne s'est produite",
                    getPath()
            );
            // Log l'erreur en production
            System.err.println("Erreur non gérée : " + exception.getMessage());
            exception.printStackTrace();
        }

        return Response.status(status)
                .entity(errorResponse)
                .build();
    }

    private String getPath() {
        return uriInfo != null ? uriInfo.getPath() : "N/A";
    }
}