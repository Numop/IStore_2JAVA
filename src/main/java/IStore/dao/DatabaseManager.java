package IStore.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "projet_istore";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            createTables();
            System.out.println("Connexion à MySQL établie avec succès.");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL non trouvé: " + e.getMessage());
            throw new RuntimeException("Driver MySQL non trouvé", e);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            throw new RuntimeException("Impossible de se connecter à la base de données MySQL", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la connexion: " + e.getMessage());
        }
        return connection;
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    pseudo VARCHAR(100) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(50) NOT NULL DEFAULT 'EMPLOYEE'
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS stores (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) UNIQUE NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS items (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    price DECIMAL(10,2) NOT NULL DEFAULT 0,
                    quantity INT NOT NULL DEFAULT 0,
                    store_id INT NOT NULL,
                    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS whitelist (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(255) UNIQUE NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS store_access (
                    user_id INT NOT NULL,
                    store_id INT NOT NULL,
                    PRIMARY KEY (user_id, store_id),
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            System.out.println("Tables MySQL créées avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création des tables: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion MySQL fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
}
