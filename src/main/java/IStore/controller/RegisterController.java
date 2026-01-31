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
 * Contrôleur pour l'écran d'inscription.
 * Gère la création de compte.
 *
 * @author IStore Team
 * @version 1.0
 */
public class RegisterController {

    @FXML private TextField emailField;
    @FXML private TextField pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private final AuthService authService = new AuthService();

    /**
     * Gère la tentative d'inscription
     */
    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String pseudo = pseudoField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        AuthService.AuthResult result = authService.register(email, pseudo, password, confirmPassword);

        if (result.isSuccess()) {
            AlertUtil.showSuccess(result.getMessage());
            navigateToLogin();
        } else {
            AlertUtil.showError("Erreur d'inscription", result.getMessage());
        }
    }

    /**
     * Retourne à l'écran de connexion
     */
    @FXML
    private void handleBackToLogin() {
        navigateToLogin();
    }

    /**
     * Navigue vers l'écran de connexion
     */
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 550));
            stage.setTitle("iStore - Connexion");
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible de charger l'écran de connexion");
            e.printStackTrace();
        }
    }
}
