package com.citymate.userapi.exception;

/**
 * Exception levée pour les erreurs de validation (400)
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}