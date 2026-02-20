FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

# Copier tout
COPY . .

# Build intelligent :
# - Si build/libs existe → skip compilation
# - Sinon → compile tout
RUN if [ -d "build/libs" ] && [ -n "$(ls -A build/libs/*.jar 2>/dev/null)" ]; then \
        echo "✅ JAR exists, skipping build"; \
    else \
        echo "🔨 Building from source..."; \
        chmod +x ./gradlew && \
        ./gradlew build -x test --no-daemon; \
    fi

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", "-jar", "app.jar"]