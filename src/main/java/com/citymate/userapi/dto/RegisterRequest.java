package com.citymate.userapi.dto;

import com.citymate.userapi.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête d'inscription
 * Utilisé dans l'endpoint POST /api/auth/register
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username est obligatoire")
    @Size(min = 3, max = 50, message = "Username doit contenir entre 3 et 50 caractères")
    private String username;

    @NotBlank(message = "Email est obligatoire")
    @Email(message = "Email doit être valide")
    private String email;

    @NotBlank(message = "Password est obligatoire")
    @Size(min = 6, message = "Password doit contenir au moins 6 caractères")
    private String password;

    private String firstName;

    private String lastName;

    private User.ProfileType profileType;
}