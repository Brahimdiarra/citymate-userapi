package com.citymate.userapi.dto;

import com.citymate.userapi.entity.User;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête de mise à jour de profil
 * Utilisé dans PUT /api/v1/users/me
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(max = 100, message = "First name trop long")
    private String firstName;

    @Size(max = 100, message = "Last name trop long")
    private String lastName;

    @Size(max = 500, message = "Bio trop longue")
    private String bio;

    @Size(max = 100, message = "City name trop long")
    private String city;

    private User.ProfileType profileType;

    @Size(max = 2048, message = "URL trop longue")
    @Pattern(
        regexp = "^(https?://.*)?$",
        message = "profilePictureUrl doit être une URL valide (http ou https)"
    )
    private String profilePictureUrl;
}