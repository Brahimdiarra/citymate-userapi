package com.citymate.userapi.mapper;

import com.citymate.userapi.dto.RegisterRequest;
import com.citymate.userapi.dto.UserDTO;
import com.citymate.userapi.entity.User;

/**
 * Mapper pour convertir entre Entity User et DTOs
 * Centralise toute la logique de conversion
 */
public class UserMapper {

    /**
     * @param request Requête d'inscription
     * @return User entity (sans password ni roles)
     */
    public static User toEntity(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setProfileType(request.getProfileType());
        user.setIsActive(true);
        user.setIsVerified(false);
        return user;
    }

    /**
     * @param user Entity User
     * @return UserDTO sans informations sensibles
     */
    public static UserDTO toDTO(User user) {
        return new UserDTO(user);
    }

    /**
     * Convertit un User Entity en UserDTO
     * Pour compatibilité et clarté du code
     */
    public static UserDTO toResponse(User user) {
        return toDTO(user);
    }
}