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

    private String accessToken;

    private String refreshToken;

    private String type = "Bearer";

    private String username;

    public JwtResponse(String accessToken, String refreshToken, String username) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.type = "Bearer";
        this.username = username;
    }
}