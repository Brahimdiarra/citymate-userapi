package com.citymate.userapi.exception;

/**
 * Exception levée pour les erreurs d'authentification (401)
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}