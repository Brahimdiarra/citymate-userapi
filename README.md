# 🔐 CityMate USER API

API d'authentification et gestion des utilisateurs pour le projet CityMate.

## 🚀 Technologies

- **Java 17**
- **Spring Boot 3.5.10**
- **Jersey (JAX-RS)** pour l'API REST
- **PostgreSQL** pour la base de données
- **JWT** pour l'authentification
- **Gradle** pour le build

## 📋 Prérequis

- Java 17
- Gradle 8.5+
- PostgreSQL 15
- Docker (pour la base de données)

## 🔧 Installation

### 1. Cloner le projet
```bash
git clone https://github.com/Brahimdiarra/citymate-userapi.git
cd citymate-user-api
```

### 2. Lancer la base de données
```bash
cd ../citymate-infrastructure
docker-compose up -d user-db
```

### 3. Configurer application.properties

Vérifier que les identifiants de la base correspondent.

### 4. Lancer l'API
```bash
./gradlew bootRun
```

L'API sera disponible sur : **http://localhost:8081**

## 📚 Documentation

- Health check : http://localhost:8081/actuator/health
- Swagger UI : http://localhost:8081/swagger-ui.html (à venir)

## 🏗️ Architecture
```
src/main/java/com/citymate/userapi/
├── model/          # Entities JPA (User, Role)
├── repository/     # Repositories Spring Data
├── security/       # Configuration JWT
├── service/        # Logique métier
└── resource/       # Endpoints Jersey (JAX-RS)
```

## 👥 Équipe

**Tech Lead** : BRAHIM  
**Projet** : Master 2 TIIL-A - Université de Bretagne Occidentale

## 📅 Date

Février 2026