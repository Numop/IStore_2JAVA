package IStore.controller;

import IStore.model.Item;
import IStore.model.Store;
import IStore.model.User;
import IStore.service.InventoryService;
import IStore.service.StoreService;
import IStore.util.AlertUtil;
import IStore.util.SessionManager;
import javafx.beans.property.SimpleDoubleProperty;
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
import java.util.Optional;

/**
 * Contrôleur pour la vue d'un magasin.
 * Gère l'inventaire et les accès employés.
 *
 * @author IStore Team
 * @version 1.0
 */
public class StoreController {

    @FXML private Label storeNameLabel;
    @FXML private TableView<Item> itemsTable;
    @FXML private TableColumn<Item, Integer> idColumn;
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, Double> priceColumn;
    @FXML private TableColumn<Item, Integer> quantityColumn;
    @FXML private ListView<User> employeesListView;
    @FXML private Button addItemBtn;
    @FXML private Button deleteItemBtn;
    @FXML private Button addEmployeeBtn;
    @FXML private Button removeEmployeeBtn;

    private Store currentStore;
    private final InventoryService inventoryService = new InventoryService();
    private final StoreService storeService = new StoreService();

    /**
     * Définit le magasin à afficher
     */
    public void setStore(Store store) {
        this.currentStore = store;
        storeNameLabel.setText("Magasin: " + store.getName());

        setupColumns();
        refreshItems();
        refreshEmployees();

        // Afficher/masquer les boutons admin
        boolean isAdmin = SessionManager.isAdmin();
        if (addItemBtn != null) addItemBtn.setVisible(isAdmin);
        if (deleteItemBtn != null) deleteItemBtn.setVisible(isAdmin);
        if (addEmployeeBtn != null) addEmployeeBtn.setVisible(isAdmin);
        if (removeEmployeeBtn != null) removeEmployeeBtn.setVisible(isAdmin);
    }

    /**
     * Configure les colonnes de la table
     */
    private void setupColumns() {
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        priceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        quantityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
    }

    /**
     * Rafraîchit la liste des articles
     */
    private void refreshItems() {
        if (currentStore != null) {
            List<Item> items = inventoryService.getItemsByStore(currentStore.getId());
            itemsTable.getItems().clear();
            itemsTable.getItems().addAll(items);
        }
    }

    /**
     * Rafraîchit la liste des employés
     */
    private void refreshEmployees() {
        if (currentStore != null) {
            List<User> employees = storeService.getStoreEmployees(currentStore.getId());
            employeesListView.getItems().clear();
            employeesListView.getItems().addAll(employees);

            // Afficher le pseudo
            employeesListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getPseudo() + " (" + user.getEmail() + ") - " + user.getRole());
                    }
                }
            });
        }
    }

    /**
     * Ajoute un nouvel article (admin)
     */
    @FXML
    private void handleAddItem() {
        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("Nouvel article");
        dialog.setHeaderText("Créer un nouvel article");

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Nom");
        TextField priceField = new TextField();
        priceField.setPromptText("Prix");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantité");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Prix:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Quantité:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                InventoryService.ServiceResult result = inventoryService.createItem(
                    nameField.getText(),
                    priceField.getText(),
                    quantityField.getText(),
                    currentStore.getId()
                );
                if (result.isSuccess()) {
                    AlertUtil.showSuccess(result.getMessage());
                    return result.getItem();
                } else {
                    AlertUtil.showError("Erreur", result.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
        refreshItems();
    }

    /**
     * Modifie l'article sélectionné (admin)
     */
    @FXML
    private void handleEditItem() {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un article");
            return;
        }

        if (!SessionManager.isAdmin()) {
            AlertUtil.showError("Erreur", "Seul un administrateur peut modifier les articles");
            return;
        }

        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'article");
        dialog.setHeaderText("Modifier: " + selectedItem.getName());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(selectedItem.getName());
        TextField priceField = new TextField(String.valueOf(selectedItem.getPrice()));
        TextField quantityField = new TextField(String.valueOf(selectedItem.getQuantity()));

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Prix:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Quantité:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                InventoryService.ServiceResult result = inventoryService.updateItem(
                    selectedItem.getId(),
                    nameField.getText(),
                    priceField.getText(),
                    quantityField.getText()
                );
                if (result.isSuccess()) {
                    AlertUtil.showSuccess(result.getMessage());
                    return result.getItem();
                } else {
                    AlertUtil.showError("Erreur", result.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
        refreshItems();
    }

    /**
     * Supprime l'article sélectionné (admin)
     */
    @FXML
    private void handleDeleteItem() {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un article");
            return;
        }

        if (AlertUtil.showConfirmation("Confirmation",
                "Êtes-vous sûr de vouloir supprimer l'article '" + selectedItem.getName() + "' ?")) {
            InventoryService.ServiceResult result = inventoryService.deleteItem(selectedItem.getId());
            if (result.isSuccess()) {
                AlertUtil.showSuccess(result.getMessage());
                refreshItems();
            } else {
                AlertUtil.showError("Erreur", result.getMessage());
            }
        }
    }

    /**
     * Augmente le stock de l'article sélectionné
     */
    @FXML
    private void handleIncreaseStock() {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un article");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Augmenter le stock");
        dialog.setHeaderText("Réception de marchandise");
        dialog.setContentText("Quantité à ajouter:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                int amount = Integer.parseInt(amountStr);
                InventoryService.ServiceResult serviceResult = inventoryService.increaseStock(selectedItem.getId(), amount);
                if (serviceResult.isSuccess()) {
                    AlertUtil.showSuccess(serviceResult.getMessage());
                    refreshItems();
                } else {
                    AlertUtil.showError("Erreur", serviceResult.getMessage());
                }
            } catch (NumberFormatException e) {
                AlertUtil.showError("Erreur", "Veuillez entrer un nombre valide");
            }
        });
    }

    /**
     * Diminue le stock de l'article sélectionné
     */
    @FXML
    private void handleDecreaseStock() {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un article");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Diminuer le stock");
        dialog.setHeaderText("Vente de marchandise");
        dialog.setContentText("Quantité à retirer:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                int amount = Integer.parseInt(amountStr);
                InventoryService.ServiceResult serviceResult = inventoryService.decreaseStock(selectedItem.getId(), amount);
                if (serviceResult.isSuccess()) {
                    AlertUtil.showSuccess(serviceResult.getMessage());
                    refreshItems();
                } else {
                    AlertUtil.showError("Erreur", serviceResult.getMessage());
                }
            } catch (NumberFormatException e) {
                AlertUtil.showError("Erreur", "Veuillez entrer un nombre valide");
            }
        });
    }

    /**
     * Ajoute un employé au magasin (admin)
     */
    @FXML
    private void handleAddEmployee() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/add-employee.fxml"));
            Parent root = loader.load();
            AddEmployeeController controller = loader.getController();
            controller.setStore(currentStore);
            controller.setOnComplete(() -> refreshEmployees());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter un employé");
            dialogStage.setScene(new Scene(root, 400, 300));
            dialogStage.showAndWait();
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible d'ouvrir le dialogue");
            e.printStackTrace();
        }
    }

    /**
     * Retire un employé du magasin (admin)
     */
    @FXML
    private void handleRemoveEmployee() {
        User selectedEmployee = employeesListView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un employé");
            return;
        }

        if (AlertUtil.showConfirmation("Confirmation",
                "Retirer l'accès de '" + selectedEmployee.getPseudo() + "' à ce magasin ?")) {
            StoreService.ServiceResult result = storeService.removeEmployeeFromStore(
                selectedEmployee.getId(), currentStore.getId());
            if (result.isSuccess()) {
                AlertUtil.showSuccess(result.getMessage());
                refreshEmployees();
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
            Stage stage = (Stage) storeNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("iStore - Tableau de bord");
        } catch (IOException e) {
            AlertUtil.showError("Erreur", "Impossible de retourner au tableau de bord");
            e.printStackTrace();
        }
    }
}
