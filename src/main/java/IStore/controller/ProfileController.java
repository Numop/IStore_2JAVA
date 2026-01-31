package IStore.controller;

import IStore.model.User;
import IStore.service.AuthService;
import IStore.service.UserService;
import IStore.util.AlertUtil;
import IStore.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contrôleur pour le profil utilisateur.
 * Permet à l'utilisateur de voir et modifier ses informations.
 *
 * @author IStore Team
 * @version 1.0
 */
public class ProfileController {

    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private TextField pseudoField;
    @FXML private TextField emailField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private final UserService userService = new UserService();
    private final AuthService authService = new AuthService();

    /**
     * Initialise le contrôleur
     */
    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            emailLabel.setText(currentUser.getEmail());
            roleLabel.setText(currentUser.isAdmin() ? "Administrateur" : "Employé");
            pseudoField.setText(currentUser.getPseudo());
            emailField.setText(currentUser.getEmail());
        }
    }

    /**
     * Sauvegarde les modifications du profil
     */
    @FXML
    private void handleSave() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            AlertUtil.showError("Erreur", "Session expirée");
            return;
        }

        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Vérifier la confirmation du nouveau mot de passe
        if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            AlertUtil.showError("Erreur", "Les nouveaux mots de passe ne correspondent pas");
            return;
        }

        UserService.ServiceResult result = userService.updateUser(
            currentUser.getId(),
            pseudoField.getText(),
            emailField.getText(),
            newPassword
        );

        if (result.isSuccess()) {
            AlertUtil.showSuccess(result.getMessage());
            // Effacer les champs de mot de passe
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            // Mettre à jour les labels
            initialize();
        } else {
            AlertUtil.showError("Erreur", result.getMessage());
        }
    }

    /**
     * Supprime le compte de l'utilisateur
     */
    @FXML
    private void handleDeleteAccount() {
        if (AlertUtil.showConfirmation("Suppression de compte",
                "Êtes-vous sûr de vouloir supprimer votre compte ?\nCette action est irréversible !")) {

            User currentUser = SessionManager.getCurrentUser();
            UserService.ServiceResult result = userService.deleteUser(currentUser.getId());

            if (result.isSuccess()) {
                AlertUtil.showSuccess("Compte supprimé avec succès");
                // Rediriger vers la page de connexion
                handleBack();
            } else {
                AlertUtil.showError("Erreur", result.getMessage());
            }
        }
    }

    /**
     * Retourne au tableau de bord (ou à la connexion si déconnecté)
     */
    @FXML
    private void handleBack() {
        try {
            String fxmlPath;
            String title;
            int width, height;

            if (SessionManager.isLoggedIn()) {
                fxmlPath = "/IStore/view/dashboard.fxml";
                title = "iStore - Tableau de bord";
                width = 950;
                height = 700;
            } else {
                fxmlPath = "/IStore/view/login.fxml";
                title = "iStore - Connexion";
                width = 400;
                height = 550;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) pseudoField.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
            stage.setTitle(title);
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Erreur de navigation");
            e.printStackTrace();
        }
    }
}
