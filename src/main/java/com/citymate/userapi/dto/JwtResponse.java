package com.citymate.userapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse contenant le token JWT
 * Renvoyé après login ou register réussi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;

    private String type = "Bearer";

    private String username;

    /**
     * Constructeur sans le type (par défaut "Bearer")
     */
    public JwtResponse(String token, String username) {
        this.token = token;
        this.type = "Bearer";
        this.username = username;
    }
}