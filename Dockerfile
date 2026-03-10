FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

# Copier les fichiers de dépendances en premier (cache Docker)
# gradle est déjà installé dans l'image de base — pas besoin de gradlew
COPY build.gradle settings.gradle ./
COPY gradle gradle

# Télécharger les dépendances (layer mis en cache si build.gradle ne change pas)
RUN gradle dependencies --no-daemon || true

# Copier le reste du code source
COPY src src

# Toujours supprimer l'ancien build et recompiler depuis les sources
RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", "-jar", "app.jar"]