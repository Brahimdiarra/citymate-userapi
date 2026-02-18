# ============================================
# CITYMATE USER API - Dockerfile
# ============================================

# ============================================
# ÉTAPE 1 : BUILD
# ============================================
FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

# Copier les fichiers de configuration Gradle
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copier le code source
COPY src ./src

# Compiler le projet (skip tests pour accélérer)
RUN gradle build -x test --no-daemon

# ============================================
# ÉTAPE 2 : RUNTIME
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/build/libs/*.jar app.jar

# Exposer le port 8081
EXPOSE 8081

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=docker

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]

# ============================================
# UTILISATION
# ============================================
# Build :
#   docker build -t citymate-user-api .
#
# Run :
#   docker run -p 8081:8081 \
#     -e SPRING_DATASOURCE_URL=jdbc:postgresql://user-db:5432/user_db \
#     -e SPRING_DATASOURCE_USERNAME=citymate_user \
#     -e SPRING_DATASOURCE_PASSWORD=user_password \
#     -e JWT_SECRET=your_secret_key \
#     citymate-user-api
# ============================================