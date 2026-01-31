package IStore.controller;

import IStore.service.AuthService;
import IStore.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contrôleur pour l'écran de connexion.
 * Gère la connexion et la navigation vers l'inscription.
 *
 * @author IStore Team
 * @version 1.0
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    /**
     * Initialise le contrôleur
     */
    @FXML
    public void initialize() {
        // Configuration initiale si nécessaire
    }

    /**
     * Gère la tentative de connexion
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        AuthService.AuthResult result = authService.login(email, password);

        if (result.isSuccess()) {
            navigateToDashboard();
        } else {
            AlertUtil.showError("Erreur de connexion", result.getMessage());
        }
    }

    /**
     * Navigue vers l'écran d'inscription
     */
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 580));
            stage.setTitle("iStore - Inscription");
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible de charger l'écran d'inscription");
            e.printStackTrace();
        }
    }

    /**
     * Navigue vers le tableau de bord
     */
    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 950, 700));
            stage.setTitle("iStore - Tableau de bord");
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible de charger le tableau de bord");
            e.printStackTrace();
        }
    }
}
