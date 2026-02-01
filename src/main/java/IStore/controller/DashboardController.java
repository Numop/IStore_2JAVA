package IStore.controller;

import IStore.model.Store;
import IStore.model.User;
import IStore.service.AuthService;
import IStore.service.StoreService;
import IStore.util.AlertUtil;
import IStore.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur pour le tableau de bord principal.
 * Point central de navigation de l'application.
 *
 * @author IStore Team
 * @version 1.0
 */
public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private VBox adminMenu;
    @FXML private ListView<Store> storeListView;
    @FXML private Button manageUsersBtn;
    @FXML private Button manageWhitelistBtn;
    @FXML private Button createStoreBtn;
    @FXML private Button deleteStoreBtn;

    private final AuthService authService = new AuthService();
    private final StoreService storeService = new StoreService();

    /**
     * Initialise le contrôleur
     */
    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser != null) {
            welcomeLabel.setText("Bienvenue, " + currentUser.getPseudo() + " !");
            roleLabel.setText("Rôle: " + (currentUser.isAdmin() ? "Administrateur" : "Employé"));

            // Afficher/Masquer les options admin
            boolean isAdmin = currentUser.isAdmin();
            if (adminMenu != null) {
                adminMenu.setVisible(isAdmin);
                adminMenu.setManaged(isAdmin);
            }
            if (manageUsersBtn != null) manageUsersBtn.setVisible(isAdmin);
            if (manageWhitelistBtn != null) manageWhitelistBtn.setVisible(isAdmin);
            if (createStoreBtn != null) createStoreBtn.setVisible(isAdmin);
            if (deleteStoreBtn != null) {
                deleteStoreBtn.setVisible(isAdmin);
                deleteStoreBtn.setManaged(isAdmin);
            }
        }

        refreshStoreList();

        // Double-clic pour ouvrir un magasin
        storeListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleOpenStore();
            }
        });
    }

    /**
     * Rafraîchit la liste des magasins
     */
    private void refreshStoreList() {
        List<Store> stores = storeService.getAccessibleStores();
        storeListView.getItems().clear();
        storeListView.getItems().addAll(stores);
    }

    /**
     * Ouvre le magasin sélectionné
     */
    @FXML
    private void handleOpenStore() {
        Store selectedStore = storeListView.getSelectionModel().getSelectedItem();
        if (selectedStore == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un magasin");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/store.fxml"));
            Parent root = loader.load();
            StoreController controller = loader.getController();
            controller.setStore(selectedStore);

            Stage stage = (Stage) storeListView.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("iStore - " + selectedStore.getName());
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible d'ouvrir le magasin");
            e.printStackTrace();
        }
    }

    /**
     * Crée un nouveau magasin (admin)
     */
    @FXML
    private void handleCreateStore() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau magasin");
        dialog.setHeaderText("Créer un nouveau magasin");
        dialog.setContentText("Nom du magasin:");

        dialog.showAndWait().ifPresent(name -> {
            StoreService.ServiceResult result = storeService.createStore(name);
            if (result.isSuccess()) {
                AlertUtil.showSuccess(result.getMessage());
                refreshStoreList();
            } else {
                AlertUtil.showError("Erreur", result.getMessage());
            }
        });
    }

    /**
     * Supprime le magasin sélectionné (admin)
     */
    @FXML
    private void handleDeleteStore() {
        Store selectedStore = storeListView.getSelectionModel().getSelectedItem();
        if (selectedStore == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un magasin");
            return;
        }

        if (AlertUtil.showConfirmation("Confirmation",
                "Êtes-vous sûr de vouloir supprimer le magasin '" + selectedStore.getName() + "' ?\n" +
                "Cette action supprimera également tous les articles associés.")) {
            StoreService.ServiceResult result = storeService.deleteStore(selectedStore.getId());
            if (result.isSuccess()) {
                AlertUtil.showSuccess(result.getMessage());
                refreshStoreList();
            } else {
                AlertUtil.showError("Erreur", result.getMessage());
            }
        }
    }

    /**
     * Ouvre la gestion des utilisateurs (admin)
     */
    @FXML
    private void handleManageUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/users.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) storeListView.getScene().getWindow();
            stage.setScene(new Scene(root, 690, 600));
            stage.setTitle("iStore - Gestion des utilisateurs");
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible d'ouvrir la gestion des utilisateurs");
            e.printStackTrace();
        }
    }

    /**
     * Ouvre la gestion de la whitelist (admin)
     */
    @FXML
    private void handleManageWhitelist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/whitelist.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) storeListView.getScene().getWindow();
            stage.setScene(new Scene(root, 650, 650));
            stage.setTitle("iStore - Whitelist");
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible d'ouvrir la whitelist");
            e.printStackTrace();
        }
    }

    /**
     * Ouvre le profil utilisateur
     */
    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/profile.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) storeListView.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 400));
            stage.setTitle("iStore - Mon profil");
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible d'ouvrir le profil");
            e.printStackTrace();
        }
    }

    /**
     * Déconnecte l'utilisateur
     */
    @FXML
    private void handleLogout() {
        if (AlertUtil.showConfirmation("Déconnexion", "Voulez-vous vraiment vous déconnecter ?")) {
            authService.logout();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) storeListView.getScene().getWindow();
                stage.setScene(new Scene(root, 450, 580));
                stage.setTitle("iStore - Connexion");
            } catch (IOException e) {
                AlertUtil.showError("Erreur", "Erreur lors de la déconnexion");
                e.printStackTrace();
            }
        }
    }
}
