package IStore.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestionnaire de connexion à la base de données SQLite.
 * Implémente le pattern Singleton pour une connexion unique.
 *
 * @author IStore Team
 * @version 1.0
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:istore.db";
    private static DatabaseManager instance;
    private Connection connection;

    /**
     * Constructeur privé - initialise la connexion et crée les tables
     */
    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        }
    }

    /**
     * Obtient l'instance unique du DatabaseManager
     * @return L'instance du DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Obtient la connexion à la base de données
     * @return La connexion SQL
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la connexion: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Crée toutes les tables nécessaires à l'application
     */
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Table des utilisateurs
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL,
                    pseudo TEXT NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL DEFAULT 'EMPLOYEE'
                )
            """);

            // Table des magasins
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS stores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL
                )
            """);

            // Table des articles (inventaire lié au magasin)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    price REAL NOT NULL DEFAULT 0,
                    quantity INTEGER NOT NULL DEFAULT 0,
                    store_id INTEGER NOT NULL,
                    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
                )
            """);

            // Table de la whitelist
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS whitelist (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL
                )
            """);

            // Table d'accès aux magasins (relation many-to-many)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS store_access (
                    user_id INTEGER NOT NULL,
                    store_id INTEGER NOT NULL,
                    PRIMARY KEY (user_id, store_id),
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
                )
            """);

            // Activer les clés étrangères
            stmt.execute("PRAGMA foreign_keys = ON");

            System.out.println("Tables créées avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création des tables: " + e.getMessage());
        }
    }

    /**
     * Ferme la connexion à la base de données
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
}
