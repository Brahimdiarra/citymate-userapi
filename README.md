# 🔐 CityMate USER API

API d'authentification et gestion des utilisateurs pour le projet CityMate.

**Tech Lead** : BRAHIM  
**Projet** : Master 2 TIIL-A - Université de Bretagne Occidentale

---

## 🚀 Technologies

- **Java 17**
- **Spring Boot 3.5.10**
- **Jersey (JAX-RS)** pour l'API REST
- **PostgreSQL 15** pour la base de données
- **JWT** pour l'authentification
- **Gradle 8.5** pour le build
- **Docker** pour le déploiement

---

## 📋 Prérequis

- Java 17
- Gradle 8.5+
- PostgreSQL 15
- Docker (optionnel mais recommandé)

---

## 🔧 Installation

### Option 1 : Avec Docker (Recommandé)
```bash
# 1. Cloner le projet
git clone https://github.com/VOTRE-USERNAME/citymate-user-api.git
cd citymate-user-api

# 2. Lancer la base de données
cd ../citymate-infrastructure
docker-compose up -d user-db

# 3. Builder l'image Docker
cd ../citymate-user-api
docker build -t citymate-user-api .

# 4. Lancer l'API avec Docker
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/user_db \
  -e SPRING_DATASOURCE_USERNAME=citymate_user \
  -e SPRING_DATASOURCE_PASSWORD=user_secure_password_123 \
  -e JWT_SECRET=VotreCleSecreteTresFortePourLaProduction \
  citymate-user-api
```

### Option 2 : Sans Docker
```bash
# 1. Cloner le projet
git clone https://github.com/VOTRE-USERNAME/citymate-user-api.git
cd citymate-user-api

# 2. Lancer PostgreSQL localement ou avec Docker
cd ../citymate-infrastructure
docker-compose up -d user-db

# 3. Configurer application.properties
# Vérifier que les identifiants correspondent

# 4. Compiler et lancer
cd ../citymate-user-api
./gradlew bootRun
```

L'API sera disponible sur : **http://localhost:8081**

---

## 📚 Documentation

### Endpoints disponibles

**Base URL** : `http://localhost:8081/api/v1`

#### Authentification (Public)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/auth/health` | Health check |
| POST | `/auth/register` | Créer un compte |
| POST | `/auth/login` | Se connecter |

#### Utilisateurs (Authentification requise)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/users/me` | Profil de l'utilisateur connecté |
| GET | `/users/{username}` | Profil public d'un utilisateur |

### Tester avec Postman

1. Importer la collection : `docs/CityMate_USER_API.postman_collection.json`
2. Suivre le guide : `docs/GUIDE_FRONTEND_TESTER_API.md`

---

## 🏗️ Architecture
```
src/main/java/com/citymate/userapi/
├── UserApiApplication.java    # Point d'entrée
├── entity/                     # Entities JPA (User, Role)
├── repository/                 # Repositories Spring Data
├── security/                   # JWT (TokenProvider, Filter, UserDetailsService)
├── config/                     # Configuration (Security, Jersey, DataInitializer)
├── dto/                        # DTOs (Request/Response)
├── service/                    # Logique métier (AuthService)
└── resource/                   # Endpoints Jersey (AuthResource, UserResource)
```

---

## 🔐 Authentification JWT

### Fonctionnement

1. **Register/Login** → Reçoit un token JWT
2. **Requêtes suivantes** → Envoyer le token dans le header :
```
   Authorization: Bearer <token>
```
3. **Token valide 24h** (86400000 ms)

### Exemple
```bash
# 1. Login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'

# Réponse
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "alice"
}

# 2. Utiliser le token
curl http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 🗄️ Base de données

### Tables créées automatiquement

- `roles` : Rôles (VISITOR, CLIENT, ADMIN)
- `users` : Utilisateurs
- `user_roles` : Association User ↔ Role

### Initialisation

Les rôles par défaut sont créés automatiquement au démarrage via `DataInitializer`.

---

## 🐳 Docker

### Build
```bash
docker build -t citymate-user-api .
```

### Run standalone
```bash
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/user_db \
  -e SPRING_DATASOURCE_USERNAME=citymate_user \
  -e SPRING_DATASOURCE_PASSWORD=user_secure_password_123 \
  -e JWT_SECRET=your_secret_key \
  citymate-user-api
```

### Avec Docker Compose (depuis citymate-infrastructure)
```bash
cd ../citymate-infrastructure
docker-compose up -d
```

---

## 🧪 Tests
```bash
# Compiler
./gradlew build

# Lancer les tests
./gradlew test

# Lancer l'application
./gradlew bootRun
```

---

## 📝 Configuration

### Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `SPRING_DATASOURCE_URL` | URL de la base PostgreSQL | `jdbc:postgresql://localhost:5432/user_db` |
| `SPRING_DATASOURCE_USERNAME` | Username PostgreSQL | `citymate_user` |
| `SPRING_DATASOURCE_PASSWORD` | Password PostgreSQL | `user_secure_password_123` |
| `JWT_SECRET` | Clé secrète JWT | *Changez en production !* |
| `JWT_EXPIRATION` | Durée validité token (ms) | `86400000` (24h) |

---

## 🔗 Intégration avec les autres APIs

Cette API partage la **même clé JWT** avec :
- **CITY API** (Port 8082)
- **COMMUNITY API** (Port 8083)

Les 3 APIs valident les tokens JWT de manière décentralisée.

---

---


## 🐛 Troubleshooting

### Erreur : "Rôle CLIENT non trouvé"
→ Les rôles n'ont pas été initialisés. Relance l'application, `DataInitializer` va les créer.

### Erreur : "Connection refused"
→ PostgreSQL n'est pas lancé. Lance : `docker-compose up -d user-db`

### Erreur : "Port 8081 already in use"
→ Une autre application utilise le port 8081. Change le port dans `application.properties`.

---


---


