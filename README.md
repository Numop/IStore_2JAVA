# 🏪 iStore - Système de Gestion d'Inventaire

Application Java complète pour la gestion d'inventaire de magasins, développée avec JavaFX et SQLite.

## 📋 Table des matières

- [Fonctionnalités](#-fonctionnalités)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Exécution](#-exécution)
- [Architecture du Projet](#-architecture-du-projet)
- [Documentation Technique](#-documentation-technique)
- [Tests](#-tests)
- [Sécurité](#-sécurité)
- [Auteurs](#-auteurs)

---

## ✨ Fonctionnalités

### Authentification
- ✅ Connexion sécurisée (email/mot de passe)
- ✅ Création de compte avec validation par whitelist
- ✅ Hashage des mots de passe avec BCrypt
- ✅ Messages d'erreur détaillés

### Gestion des Utilisateurs
- ✅ CRUD complet (Créer, Lire, Modifier, Supprimer)
- ✅ Rôles : **Admin** et **Employé**
- ✅ Un utilisateur peut modifier/supprimer son propre compte
- ✅ Les admins peuvent gérer tous les comptes

### Gestion des Magasins
- ✅ Création/suppression de magasins (Admin uniquement)
- ✅ Attribution d'employés aux magasins
- ✅ Liste des employés par magasin
- ✅ Accès restreint selon les permissions

### Gestion de l'Inventaire
- ✅ Ajout/modification/suppression d'articles (Admin)
- ✅ Consultation de l'inventaire par magasin
- ✅ Augmentation/diminution du stock (employés autorisés)
- ✅ Protection contre les stocks négatifs

### Administration
- ✅ Whitelist des emails autorisés à s'inscrire
- ✅ Gestion des rôles utilisateurs
- ✅ Premier utilisateur automatiquement Admin

---

## 💻 Prérequis

- **Java JDK 19** ou supérieur
- **Maven 3.8+**
- **MySQL 8.0+** (avec le serveur en cours d'exécution)
- Connexion Internet (pour télécharger les dépendances)

### Configuration de la base de données MySQL

1. Démarrer le serveur MySQL
2. Créer la base de données :

```sql
CREATE DATABASE db_IStore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Vérifier la configuration dans `DatabaseManager.java` :
   - **Host** : `localhost`
   - **Port** : `3306`
   - **Database** : `db_IStore`
   - **User** : `root`
   - **Password** : `` (vide par défaut, modifiez si nécessaire)

### Vérifier les versions installées

```bash
java -version
mvn -version
mysql --version
```

---

## 📦 Installation

### 1. Cloner ou extraire le projet

```bash
cd IStore_2JAVA
```

### 2. Installer les dépendances Maven

```bash
mvn clean install
```

Cette commande va :
- Télécharger JavaFX 19.0.2
- Télécharger MySQL Connector/J
- Télécharger BCrypt
- Télécharger JUnit 5 (pour les tests)
- Compiler le projet

---

## 🚀 Exécution

### Méthode 1 : Via Maven (recommandé)

```bash
mvn javafx:run
```

### Méthode 2 : Via IntelliJ IDEA

1. Ouvrir le projet dans IntelliJ IDEA
2. Attendre que Maven synchronise les dépendances
3. Exécuter la classe `IStore.Main`

### Premier lancement

⚠️ **Important** : Le **premier utilisateur** créé sera automatiquement **administrateur**.

1. Cliquez sur "Créer un compte"
2. Remplissez les informations (email, pseudo, mot de passe)
3. Vous serez admin et pourrez ajouter d'autres emails à la whitelist

---

## 🏗 Architecture du Projet

```
src/main/java/IStore/
├── Main.java                    # Point d'entrée JavaFX
│
├── model/                       # Entités (POJO)
│   ├── User.java               # Utilisateur (id, email, pseudo, password, role)
│   ├── Store.java              # Magasin (id, name)
│   ├── Item.java               # Article (id, name, price, quantity, storeId)
│   ├── Whitelist.java          # Email autorisé
│   ├── StoreAccess.java        # Relation User-Store
│   └── Role.java               # Enum (ADMIN, EMPLOYEE)
│
├── dao/                         # Data Access Objects (accès BDD)
│   ├── DatabaseManager.java    # Singleton de connexion SQLite
│   ├── UserDAO.java            # CRUD utilisateurs
│   ├── StoreDAO.java           # CRUD magasins
│   ├── ItemDAO.java            # CRUD articles
│   ├── WhitelistDAO.java       # CRUD whitelist
│   └── StoreAccessDAO.java     # Gestion accès magasins
│
├── service/                     # Logique métier
│   ├── AuthService.java        # Authentification
│   ├── UserService.java        # Gestion utilisateurs
│   ├── StoreService.java       # Gestion magasins
│   ├── InventoryService.java   # Gestion inventaire
│   └── WhitelistService.java   # Gestion whitelist
│
├── controller/                  # Contrôleurs JavaFX
│   ├── LoginController.java
│   ├── RegisterController.java
│   ├── DashboardController.java
│   ├── StoreController.java
│   ├── UserManagementController.java
│   ├── WhitelistController.java
│   ├── ProfileController.java
│   └── AddEmployeeController.java
│
└── util/                        # Utilitaires
    ├── PasswordUtil.java       # Hashage BCrypt
    ├── ValidationUtil.java     # Validation des entrées
    ├── SessionManager.java     # Gestion de session
    └── AlertUtil.java          # Alertes JavaFX

src/main/resources/IStore/
├── view/                        # Fichiers FXML (interfaces)
│   ├── login.fxml
│   ├── register.fxml
│   ├── dashboard.fxml
│   ├── store.fxml
│   ├── users.fxml
│   ├── whitelist.fxml
│   ├── profile.fxml
│   └── add-employee.fxml
│
└── css/
    └── styles.css              # Styles de l'application

src/test/java/IStore/           # Tests unitaires
├── service/
│   └── AuthServiceTest.java
└── util/
    ├── PasswordUtilTest.java
    └── ValidationUtilTest.java
```

---

## 📚 Documentation Technique

### Pattern MVC

L'application suit le pattern **Model-View-Controller** :

| Couche | Rôle | Packages |
|--------|------|----------|
| **Model** | Entités et données | `model/` |
| **View** | Interface utilisateur | `resources/view/` (FXML) |
| **Controller** | Logique de présentation | `controller/` |
| **Service** | Logique métier | `service/` |
| **DAO** | Accès aux données | `dao/` |

### Base de données

SQLite est utilisé avec une base de données locale `istore.db`.

#### Tables

| Table | Description |
|-------|-------------|
| `users` | Utilisateurs (id, email, pseudo, password, role) |
| `stores` | Magasins (id, name) |
| `items` | Articles (id, name, price, quantity, store_id) |
| `whitelist` | Emails autorisés (id, email) |
| `store_access` | Accès utilisateur-magasin (user_id, store_id) |

### Diagramme de classes simplifié

```
┌─────────────┐       ┌─────────────┐
│    User     │       │    Store    │
├─────────────┤       ├─────────────┤
│ id          │       │ id          │
│ email       │       │ name        │
│ pseudo      │◄─────►│             │
│ password    │  N:M  └──────┬──────┘
│ role        │              │ 1:N
└─────────────┘              │
                             ▼
                      ┌─────────────┐
                      │    Item     │
                      ├─────────────┤
                      │ id          │
                      │ name        │
                      │ price       │
                      │ quantity    │
                      │ store_id    │
                      └─────────────┘
```

---

## 🧪 Tests

### Exécuter les tests

```bash
mvn test
```

### Tests disponibles

| Classe | Description |
|--------|-------------|
| `AuthServiceTest` | Tests d'authentification (login, register) |
| `PasswordUtilTest` | Tests de hashage BCrypt |
| `ValidationUtilTest` | Tests de validation des entrées |

---

## 🔐 Sécurité

### Hashage des mots de passe

- Utilisation de **BCrypt** avec un coût de 12 rounds
- Chaque mot de passe a un **salt unique**
- Les mots de passe ne sont **jamais** stockés en clair

### Contrôle d'accès

| Action | Admin | Employé | Non connecté |
|--------|:-----:|:-------:|:------------:|
| Voir les magasins | Tous | Assignés seulement | ❌ |
| Créer un magasin | ✅ | ❌ | ❌ |
| Supprimer un magasin | ✅ | ❌ | ❌ |
| Créer un article | ✅ | ❌ | ❌ |
| Modifier le stock | ✅ | ✅ (si accès) | ❌ |
| Gérer les utilisateurs | ✅ | ❌ | ❌ |
| Whitelist emails | ✅ | ❌ | ❌ |
| Créer un compte | - | - | ✅ (si whitelisté) |

### Validation des entrées

- Email : format valide vérifié par regex
- Mot de passe : minimum 6 caractères
- Pseudo : minimum 2 caractères
- Prix : nombre positif
- Quantité : entier positif

---

## 👥 Auteurs

**IStore Team**

Projet réalisé dans le cadre d'un projet académique.

---

## 📄 Licence

Ce projet est fourni à des fins éducatives.

---

## ❓ FAQ

### Le projet ne compile pas ?

```bash
mvn clean install -U
```

### L'application ne démarre pas ?

Vérifiez que Java 19+ est installé et que `JAVA_HOME` est configuré.

### Comment réinitialiser la base de données ?

Supprimez le fichier `istore.db` à la racine du projet. Une nouvelle base sera créée au prochain lancement.

### Comment devenir admin ?

- Soit vous êtes le **premier utilisateur** créé
- Soit un admin existant change votre rôle

---

*iStore v1.0 - 2024*
