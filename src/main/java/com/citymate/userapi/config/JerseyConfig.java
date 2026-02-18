package com.citymate.userapi.config;

import com.citymate.userapi.resource.AuthResource;
import com.citymate.userapi.resource.UserResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import jakarta.ws.rs.ApplicationPath;

/**
 * Configuration Jersey (JAX-RS)
 * Définit le chemin de base de l'API : /api/v1
 * Enregistre les Resources
 */
@Configuration
@ApplicationPath("/api/v1")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        // Enregistrer les Resources
        register(AuthResource.class);
        register(UserResource.class);

        // Activer les features Jersey
        packages("com.citymate.userapi.resource");
    }
}