package IStore.service;

import IStore.dao.ItemDAO;
import IStore.dao.StoreAccessDAO;
import IStore.model.Item;
import IStore.model.User;
import IStore.util.SessionManager;
import IStore.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion de l'inventaire.
 * Gère les opérations sur les articles avec contrôle d'accès.
 *
 * @author IStore Team
 * @version 1.0
 */
public class InventoryService {
    private final ItemDAO itemDAO;
    private final StoreAccessDAO storeAccessDAO;

    public InventoryService() {
        this.itemDAO = new ItemDAO();
        this.storeAccessDAO = new StoreAccessDAO();
    }

    /**
     * Résultat d'une opération
     */
    public static class ServiceResult {
        private final boolean success;
        private final String message;
        private final Item item;

        public ServiceResult(boolean success, String message) {
            this(success, message, null);
        }

        public ServiceResult(boolean success, String message, Item item) {
            this.success = success;
            this.message = message;
            this.item = item;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Item getItem() { return item; }
    }

    /**
     * Vérifie si l'utilisateur a accès au magasin
     */
    private boolean hasStoreAccess(int storeId) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        if (currentUser.isAdmin()) {
            return true;
        }
        return storeAccessDAO.hasAccess(currentUser.getId(), storeId);
    }

    /**
     * Crée un nouvel article (admin uniquement)
     * @param name Le nom de l'article
     * @param priceStr Le prix (en string)
     * @param quantityStr La quantité (en string)
     * @param storeId L'ID du magasin
     * @return Le résultat de l'opération
     */
    public ServiceResult createItem(String name, String priceStr, String quantityStr, int storeId) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut créer des articles");
        }

        // Validations
        String nameError = ValidationUtil.validateItemName(name);
        if (nameError != null) {
            return new ServiceResult(false, nameError);
        }

        String priceError = ValidationUtil.validatePrice(priceStr);
        if (priceError != null) {
            return new ServiceResult(false, priceError);
        }

        String quantityError = ValidationUtil.validateQuantity(quantityStr);
        if (quantityError != null) {
            return new ServiceResult(false, quantityError);
        }

        double price = Double.parseDouble(priceStr.trim());
        int quantity = Integer.parseInt(quantityStr.trim());

        Item item = new Item(name.trim(), price, quantity, storeId);
        Item createdItem = itemDAO.create(item);

        if (createdItem != null) {
            return new ServiceResult(true, "Article créé avec succès", createdItem);
        }

        return new ServiceResult(false, "Erreur lors de la création de l'article");
    }

    /**
     * Récupère tous les articles d'un magasin
     * @param storeId L'ID du magasin
     * @return Liste des articles
     */
    public List<Item> getItemsByStore(int storeId) {
        if (!hasStoreAccess(storeId)) {
            return List.of();
        }
        return itemDAO.findByStoreId(storeId);
    }

    /**
     * Récupère un article par son ID
     * @param itemId L'ID de l'article
     * @return L'article ou null
     */
    public Item getItemById(int itemId) {
        Optional<Item> itemOpt = itemDAO.findById(itemId);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            if (hasStoreAccess(item.getStoreId())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Met à jour un article (admin uniquement)
     * @param itemId L'ID de l'article
     * @param name Le nouveau nom
     * @param priceStr Le nouveau prix
     * @param quantityStr La nouvelle quantité
     * @return Le résultat de l'opération
     */
    public ServiceResult updateItem(int itemId, String name, String priceStr, String quantityStr) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut modifier les articles");
        }

        Optional<Item> itemOpt = itemDAO.findById(itemId);
        if (itemOpt.isEmpty()) {
            return new ServiceResult(false, "Article non trouvé");
        }

        // Validations
        String nameError = ValidationUtil.validateItemName(name);
        if (nameError != null) {
            return new ServiceResult(false, nameError);
        }

        String priceError = ValidationUtil.validatePrice(priceStr);
        if (priceError != null) {
            return new ServiceResult(false, priceError);
        }

        String quantityError = ValidationUtil.validateQuantity(quantityStr);
        if (quantityError != null) {
            return new ServiceResult(false, quantityError);
        }

        Item item = itemOpt.get();
        item.setName(name.trim());
        item.setPrice(Double.parseDouble(priceStr.trim()));
        item.setQuantity(Integer.parseInt(quantityStr.trim()));

        if (itemDAO.update(item)) {
            return new ServiceResult(true, "Article mis à jour avec succès", item);
        }

        return new ServiceResult(false, "Erreur lors de la mise à jour");
    }

    /**
     * Supprime un article (admin uniquement)
     * @param itemId L'ID de l'article à supprimer
     * @return Le résultat de l'opération
     */
    public ServiceResult deleteItem(int itemId) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut supprimer des articles");
        }

        if (itemDAO.delete(itemId)) {
            return new ServiceResult(true, "Article supprimé avec succès");
        }

        return new ServiceResult(false, "Erreur lors de la suppression");
    }

    /**
     * Augmente la quantité d'un article (employé avec accès ou admin)
     * @param itemId L'ID de l'article
     * @param amount La quantité à ajouter
     * @return Le résultat de l'opération
     */
    public ServiceResult increaseStock(int itemId, int amount) {
        Optional<Item> itemOpt = itemDAO.findById(itemId);
        if (itemOpt.isEmpty()) {
            return new ServiceResult(false, "Article non trouvé");
        }

        Item item = itemOpt.get();

        if (!hasStoreAccess(item.getStoreId())) {
            return new ServiceResult(false, "Vous n'avez pas accès à ce magasin");
        }

        if (amount <= 0) {
            return new ServiceResult(false, "La quantité doit être positive");
        }

        int newQuantity = item.getQuantity() + amount;
        if (itemDAO.updateQuantity(itemId, newQuantity)) {
            item.setQuantity(newQuantity);
            return new ServiceResult(true, "Stock augmenté de " + amount + " unités", item);
        }

        return new ServiceResult(false, "Erreur lors de la mise à jour du stock");
    }

    /**
     * Diminue la quantité d'un article (employé avec accès ou admin)
     * @param itemId L'ID de l'article
     * @param amount La quantité à retirer
     * @return Le résultat de l'opération
     */
    public ServiceResult decreaseStock(int itemId, int amount) {
        Optional<Item> itemOpt = itemDAO.findById(itemId);
        if (itemOpt.isEmpty()) {
            return new ServiceResult(false, "Article non trouvé");
        }

        Item item = itemOpt.get();

        if (!hasStoreAccess(item.getStoreId())) {
            return new ServiceResult(false, "Vous n'avez pas accès à ce magasin");
        }

        if (amount <= 0) {
            return new ServiceResult(false, "La quantité doit être positive");
        }

        if (item.getQuantity() < amount) {
            return new ServiceResult(false, "Stock insuffisant. Stock actuel: " + item.getQuantity());
        }

        int newQuantity = item.getQuantity() - amount;
        if (itemDAO.updateQuantity(itemId, newQuantity)) {
            item.setQuantity(newQuantity);
            return new ServiceResult(true, "Stock diminué de " + amount + " unités", item);
        }

        return new ServiceResult(false, "Erreur lors de la mise à jour du stock");
    }
}
