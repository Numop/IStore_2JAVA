package IStore.controller;

import IStore.model.Store;
import IStore.model.User;
import IStore.service.StoreService;
import IStore.service.UserService;
import IStore.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

/**
 * Contrôleur pour ajouter un employé à un magasin.
 *
 * @author IStore Team
 * @version 1.0
 */
public class AddEmployeeController {

    @FXML private ListView<User> usersListView;

    private Store currentStore;
    private Runnable onComplete;
    private final UserService userService = new UserService();
    private final StoreService storeService = new StoreService();

    /**
     * Initialise le contrôleur
     */
    @FXML
    public void initialize() {
        // Configurer l'affichage de la liste
        usersListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getPseudo() + " (" + user.getEmail() + ")");
                }
            }
        });
    }

    /**
     * Définit le magasin cible
     */
    public void setStore(Store store) {
        this.currentStore = store;
        loadUsers();
    }

    /**
     * Définit le callback de complétion
     */
    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    /**
     * Charge les utilisateurs qui ne sont pas encore assignés au magasin
     */
    private void loadUsers() {
        List<User> allUsers = userService.getAllUsers();
        List<User> storeEmployees = storeService.getStoreEmployees(currentStore.getId());

        // Filtrer pour ne montrer que les utilisateurs non assignés
        List<User> availableUsers = allUsers.stream()
            .filter(user -> storeEmployees.stream()
                .noneMatch(emp -> emp.getId() == user.getId()))
            .toList();

        usersListView.getItems().clear();
        usersListView.getItems().addAll(availableUsers);
    }

    /**
     * Ajoute l'utilisateur sélectionné au magasin
     */
    @FXML
    private void handleAdd() {
        User selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un utilisateur");
            return;
        }

        StoreService.ServiceResult result = storeService.addEmployeeToStore(
            selectedUser.getId(), currentStore.getId());

        if (result.isSuccess()) {
            AlertUtil.showSuccess(result.getMessage());
            if (onComplete != null) {
                onComplete.run();
            }
            closeDialog();
        } else {
            AlertUtil.showError("Erreur", result.getMessage());
        }
    }

    /**
     * Ferme le dialogue
     */
    @FXML
    private void handleCancel() {
        closeDialog();
    }

    /**
     * Ferme la fenêtre
     */
    private void closeDialog() {
        Stage stage = (Stage) usersListView.getScene().getWindow();
        stage.close();
    }
}
