package com.citymate.userapi.config;

import com.citymate.userapi.entity.Role;
import com.citymate.userapi.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initialise les données de base au démarrage de l'application
 * - Crée les rôles (VISITOR, CLIENT, ADMIN) s'ils n'existent pas
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(" Initialisation des données...");

        // Créer le rôle VISITOR s'il n'existe pas
        if (!roleRepository.existsByName("VISITOR")) {
            Role visitorRole = new Role("VISITOR", "Utilisateur non inscrit avec accès limité");
            roleRepository.save(visitorRole);
            System.out.println(" Rôle VISITOR créé");
        } else {
            System.out.println(" Rôle VISITOR existe déjà");
        }

        // Créer le rôle CLIENT s'il n'existe pas
        if (!roleRepository.existsByName("CLIENT")) {
            Role clientRole = new Role("CLIENT", "Utilisateur inscrit standard");
            roleRepository.save(clientRole);
            System.out.println(" Rôle CLIENT créé");
        } else {
            System.out.println(" Rôle CLIENT existe déjà");
        }

        // Créer le rôle ADMIN s'il n'existe pas
        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = new Role("ADMIN", "Administrateur avec tous les droits");
            roleRepository.save(adminRole);
            System.out.println("Rôle ADMIN créé");
        } else {
            System.out.println("  Rôle ADMIN existe déjà");
        }

        System.out.println("Initialisation terminée !\n");
    }
}