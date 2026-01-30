package IStore.controller;

import IStore.model.User;
import IStore.model.Role;
import IStore.service.UserService;
import IStore.util.AlertUtil;
import IStore.util.SessionManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur pour la gestion des utilisateurs.
 * Permet aux admins de voir et gérer tous les utilisateurs.
 *
 * @author IStore Team
 * @version 1.0
 */
public class UserManagementController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> pseudoColumn;
    @FXML private TableColumn<User, String> roleColumn;

    private final UserService userService = new UserService();

    /**
     * Initialise le contrôleur
     */
    @FXML
    public void initialize() {
        setupColumns();
        refreshUsers();
    }

    /**
     * Configure les colonnes de la table
     */
    private void setupColumns() {
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        pseudoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPseudo()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().name()));
    }

    /**
     * Rafraîchit la liste des utilisateurs
     */
    private void refreshUsers() {
        List<User> users = userService.getAllUsers();
        usersTable.getItems().clear();
        usersTable.getItems().addAll(users);
    }

    /**
     * Modifie l'utilisateur sélectionné
     */
    @FXML
    private void handleEditUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un utilisateur");
            return;
        }

        // Récupérer les vraies données de l'utilisateur
        User fullUser = userService.getUserById(selectedUser.getId());
        if (fullUser == null) {
            AlertUtil.showError("Erreur", "Utilisateur non trouvé");
            return;
        }

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'utilisateur");
        dialog.setHeaderText("Modifier: " + fullUser.getPseudo());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField emailField = new TextField(fullUser.getEmail());
        TextField pseudoField = new TextField(fullUser.getPseudo());
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Laisser vide pour ne pas changer");

        ComboBox<Role> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(Role.values());
        roleComboBox.setValue(fullUser.getRole());

        // Seul un admin peut changer le rôle et pas son propre rôle
        boolean canChangeRole = SessionManager.isAdmin() &&
                                SessionManager.getCurrentUser().getId() != fullUser.getId();
        roleComboBox.setDisable(!canChangeRole);

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Pseudo:"), 0, 1);
        grid.add(pseudoField, 1, 1);
        grid.add(new Label("Nouveau mot de passe:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Rôle:"), 0, 3);
        grid.add(roleComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Mise à jour des infos de base
                UserService.ServiceResult result = userService.updateUser(
                    fullUser.getId(),
                    pseudoField.getText(),
                    emailField.getText(),
                    passwordField.getText()
                );

                if (!result.isSuccess()) {
                    AlertUtil.showError("Erreur", result.getMessage());
                    return false;
                }

                // Mise à jour du rôle si autorisé et changé
                if (canChangeRole && roleComboBox.getValue() != fullUser.getRole()) {
                    UserService.ServiceResult roleResult = userService.updateUserRole(
                        fullUser.getId(), roleComboBox.getValue());
                    if (!roleResult.isSuccess()) {
                        AlertUtil.showError("Erreur", roleResult.getMessage());
                        return false;
                    }
                }

                AlertUtil.showSuccess("Utilisateur mis à jour avec succès");
                return true;
            }
            return false;
        });

        dialog.showAndWait();
        refreshUsers();
    }

    /**
     * Supprime l'utilisateur sélectionné
     */
    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un utilisateur");
            return;
        }

        // Ne pas permettre de se supprimer soi-même depuis cette interface
        if (selectedUser.getId() == SessionManager.getCurrentUserId()) {
            AlertUtil.showWarning("Attention", "Vous ne pouvez pas vous supprimer depuis cette interface. Utilisez votre profil.");
            return;
        }

        if (AlertUtil.showConfirmation("Confirmation",
                "Êtes-vous sûr de vouloir supprimer l'utilisateur '" + selectedUser.getPseudo() + "' ?")) {
            UserService.ServiceResult result = userService.deleteUser(selectedUser.getId());
            if (result.isSuccess()) {
                AlertUtil.showSuccess(result.getMessage());
                refreshUsers();
            } else {
                AlertUtil.showError("Erreur", result.getMessage());
            }
        }
    }

    /**
     * Retourne au tableau de bord
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usersTable.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("iStore - Tableau de bord");
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible de retourner au tableau de bord");
            e.printStackTrace();
        }
    }
}
