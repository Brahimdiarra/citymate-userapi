package com.citymate.userapi.config;

import com.citymate.userapi.entity.Role;
import com.citymate.userapi.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initialise les données de base au démarrage de l'application
 * - Crée les rôles (VISITOR, CLIENT, ADMIN, STUDENT) s'ils n'existent pas
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initialisation des données...");

        if (!roleRepository.existsByName("VISITOR")) {
            roleRepository.save(new Role("VISITOR", "Utilisateur non inscrit avec accès limité"));
            log.info("Rôle VISITOR créé");
        } else {
            log.debug("Rôle VISITOR existe déjà");
        }

        if (!roleRepository.existsByName("CLIENT")) {
            roleRepository.save(new Role("CLIENT", "Utilisateur inscrit standard"));
            log.info("Rôle CLIENT créé");
        } else {
            log.debug("Rôle CLIENT existe déjà");
        }

        if (!roleRepository.existsByName("ADMIN")) {
            roleRepository.save(new Role("ADMIN", "Administrateur avec tous les droits"));
            log.info("Rôle ADMIN créé");
        } else {
            log.debug("Rôle ADMIN existe déjà");
        }

        if (!roleRepository.existsByName("STUDENT")) {
            roleRepository.save(new Role("STUDENT", "Étudiant inscrit sur CityMate"));
            log.info("Rôle STUDENT créé");
        } else {
            log.debug("Rôle STUDENT existe déjà");
        }

        log.info("Initialisation terminée !");
    }
}