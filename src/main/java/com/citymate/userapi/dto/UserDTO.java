package com.citymate.userapi.dto;

import com.citymate.userapi.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les informations utilisateur
 * N'expose PAS le passwordHash ni les infos sensibles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String bio;
    private String city;
    private User.ProfileType profileType;
    private Boolean isActive;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    /**
     * Constructeur qui convertit une Entity User en DTO
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.profilePictureUrl = user.getProfilePictureUrl();
        this.bio = user.getBio();
        this.city = user.getCity();
        this.profileType = user.getProfileType();
        this.isActive = user.getIsActive();
        this.isVerified = user.getIsVerified();
        this.createdAt = user.getCreatedAt();
    }
}