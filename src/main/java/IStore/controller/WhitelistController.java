package IStore.controller;

import IStore.model.Whitelist;
import IStore.service.WhitelistService;
import IStore.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur pour la gestion de la whitelist.
 * Permet aux admins de gérer les emails autorisés à s'inscrire.
 *
 * @author IStore Team
 * @version 1.0
 */
public class WhitelistController {

    @FXML private ListView<Whitelist> whitelistView;
    @FXML private TextField emailField;

    private final WhitelistService whitelistService = new WhitelistService();

    /**
     * Initialise le contrôleur
     */
    @FXML
    public void initialize() {
        refreshWhitelist();

        // Afficher l'email dans la liste
        whitelistView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Whitelist item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getEmail());
                }
            }
        });
    }

    /**
     * Rafraîchit la liste des emails whitelistés
     */
    private void refreshWhitelist() {
        List<Whitelist> whitelist = whitelistService.getAllWhitelistedEmails();
        whitelistView.getItems().clear();
        whitelistView.getItems().addAll(whitelist);
    }

    /**
     * Ajoute un email à la whitelist
     */
    @FXML
    private void handleAddEmail() {
        String email = emailField.getText();

        WhitelistService.ServiceResult result = whitelistService.addEmail(email);
        if (result.isSuccess()) {
            AlertUtil.showSuccess(result.getMessage());
            emailField.clear();
            refreshWhitelist();
        } else {
            AlertUtil.showError("Erreur", result.getMessage());
        }
    }

    /**
     * Supprime l'email sélectionné de la whitelist
     */
    @FXML
    private void handleRemoveEmail() {
        Whitelist selected = whitelistView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un email");
            return;
        }

        if (AlertUtil.showConfirmation("Confirmation",
                "Retirer '" + selected.getEmail() + "' de la whitelist ?")) {
            WhitelistService.ServiceResult result = whitelistService.removeEmail(selected.getId());
            if (result.isSuccess()) {
                AlertUtil.showSuccess(result.getMessage());
                refreshWhitelist();
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
            Stage stage = (Stage) whitelistView.getScene().getWindow();
            stage.setScene(new Scene(root, 950, 700));
            stage.setTitle("iStore - Tableau de bord");
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible de retourner au tableau de bord");
            e.printStackTrace();
        }
    }
}
