package com.citymate.userapi.repository;

import com.citymate.userapi.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour gérer les rôles
 * Spring génère automatiquement les méthodes CRUD
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Trouver un rôle par son nom
     * @param name Nom du rôle
     * @return Optional contenant le rôle si trouvé
     */
    Optional<Role> findByName(String name);

    /**
     * Vérifier si un rôle existe par son nom
     * @param name Nom du rôle
     * @return true si existe, false sinon
     */
    Boolean existsByName(String name);
}