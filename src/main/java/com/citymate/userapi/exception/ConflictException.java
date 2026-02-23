package com.citymate.userapi.exception;

/**
 * Exception levée pour les conflits (409)
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}