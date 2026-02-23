package com.citymate.userapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO standardisé pour les réponses d'erreur
 * Retourné automatiquement par le GlobalExceptionHandler
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'erreur standardisée")
public class ErrorResponse {

    @Schema(description = "Timestamp de l'erreur")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Code HTTP")
    private int status;

    @Schema(description = "Type d'erreur")
    private String error;

    @Schema(description = "Message d'erreur détaillé")
    private String message;

    @Schema(description = "Chemin de l'endpoint appelé")
    private String path;

    /**
     * Constructeur simplifié
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}