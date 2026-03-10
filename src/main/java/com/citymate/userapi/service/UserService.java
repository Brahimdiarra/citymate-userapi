package com.citymate.userapi.service;

import com.citymate.userapi.dto.PublicUserDTO;
import com.citymate.userapi.dto.UpdateProfileRequest;
import com.citymate.userapi.dto.UserDTO;
import com.citymate.userapi.entity.User;
import com.citymate.userapi.exception.ResourceNotFoundException;
import com.citymate.userapi.mapper.UserMapper;
import com.citymate.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour gérer les utilisateurs
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Récupérer son propre profil complet (données privées incluses)
     */
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return UserMapper.toDTO(user);
    }

    /**
     * Récupérer le profil public d'un autre utilisateur (données limitées)
     * Ne retourne que les champs visibles publiquement
     */
    public PublicUserDTO getPublicUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return new PublicUserDTO(user);
    }

    /**
     * Mettre à jour le profil d'un utilisateur
     */
    @Transactional
    public UserDTO updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));


        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getProfileType() != null) {
            user.setProfileType(request.getProfileType());
        }
        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        userRepository.save(user);

        return UserMapper.toDTO(user);
    }
}