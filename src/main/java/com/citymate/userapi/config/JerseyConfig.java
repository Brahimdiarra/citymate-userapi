package com.citymate.userapi.config;

import com.citymate.userapi.resource.AuthResource;
import com.citymate.userapi.resource.UserResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import jakarta.ws.rs.ApplicationPath;

/**
 * Configuration Jersey (JAX-RS)
 * Définit le chemin de base de l'API : /api/v1
 * Enregistre les Resources
 * Configure OpenAPI/Swagger pour l'auto-documentation
 */
@Configuration
@ApplicationPath("/api/v1")
@OpenAPIDefinition(info = @Info(title = "CityMate User API", version = "1.0.0", description = "API REST pour la gestion des utilisateurs et l'authentification", contact = @Contact(name = "CityMate Team", email = "support@citymate.com"), license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")))
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Entrez le token JWT fourni par /auth/login")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        // Enregistrer les Resources
        register(AuthResource.class);
        register(UserResource.class);

        // Swagger OpenAPI endpoint : GET /api/v1/openapi.json
        // Enregistré séparément pour ne pas être documenté dans la spec
        register(OpenApiResource.class);
    }
}