package com.citymate.userapi.repository;

import com.citymate.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour gérer les utilisateurs
 * Spring génère automatiquement les méthodes CRUD
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouver un utilisateur par username
     * @param username Username de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByUsername(String username);

    /**
     * Trouver un utilisateur par email
     * @param email Email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifier si un username existe déjà
     * @param username Username à vérifier
     * @return true si existe, false sinon
     */
    Boolean existsByUsername(String username);

    /**
     * Vérifier si un email existe déjà
     * @param email Email à vérifier
     * @return true si existe, false sinon
     */
    Boolean existsByEmail(String email);
}