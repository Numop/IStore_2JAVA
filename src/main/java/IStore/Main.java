package IStore;

import IStore.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application iStore.
 * Application de gestion d'inventaire pour magasins.
 *
 * <p>Fonctionnalités principales:</p>
 * <ul>
 *   <li>Authentification sécurisée avec BCrypt</li>
 *   <li>Gestion des utilisateurs (CRUD)</li>
 *   <li>Gestion des magasins et inventaires</li>
 *   <li>Contrôle d'accès basé sur les rôles (Admin/Employee)</li>
 *   <li>Whitelist des emails pour l'inscription</li>
 * </ul>
 *
 * @author IStore Team
 * @version 1.0
 */
public class Main extends Application {

    /**
     * Démarre l'application JavaFX
     *
     * @param primaryStage La fenêtre principale
     * @throws Exception Si erreur de chargement
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialiser la base de données
        DatabaseManager.getInstance();

        // Charger l'écran de connexion
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 550);

        primaryStage.setTitle("iStore - Connexion");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }

    /**
     * Appelé lors de la fermeture de l'application
     */
    @Override
    public void stop() {
        // Fermer proprement la connexion à la base de données
        DatabaseManager.getInstance().closeConnection();
    }

    /**
     * Point d'entrée principal
     *
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
}