package IStore.service;

import IStore.dao.StoreDAO;
import IStore.dao.StoreAccessDAO;
import IStore.dao.ItemDAO;
import IStore.model.Store;
import IStore.model.User;
import IStore.util.SessionManager;
import IStore.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des magasins.
 * Gère les opérations CRUD sur les magasins avec contrôle d'accès.
 *
 * @author IStore Team
 * @version 1.0
 */
public class StoreService {
    private final StoreDAO storeDAO;
    private final StoreAccessDAO storeAccessDAO;
    private final ItemDAO itemDAO;

    public StoreService() {
        this.storeDAO = new StoreDAO();
        this.storeAccessDAO = new StoreAccessDAO();
        this.itemDAO = new ItemDAO();
    }

    /**
     * Résultat d'une opération
     */
    public static class ServiceResult {
        private final boolean success;
        private final String message;
        private final Store store;

        public ServiceResult(boolean success, String message) {
            this(success, message, null);
        }

        public ServiceResult(boolean success, String message, Store store) {
            this.success = success;
            this.message = message;
            this.store = store;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Store getStore() { return store; }
    }

    /**
     * Crée un nouveau magasin (admin uniquement)
     * @param name Le nom du magasin
     * @return Le résultat de l'opération
     */
    public ServiceResult createStore(String name) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut créer un magasin");
        }

        String nameError = ValidationUtil.validateStoreName(name);
        if (nameError != null) {
            return new ServiceResult(false, nameError);
        }

        if (storeDAO.nameExists(name)) {
            return new ServiceResult(false, "Un magasin avec ce nom existe déjà");
        }

        Store store = new Store(name.trim());
        Store createdStore = storeDAO.create(store);

        if (createdStore != null) {
            return new ServiceResult(true, "Magasin créé avec succès", createdStore);
        }

        return new ServiceResult(false, "Erreur lors de la création du magasin");
    }

    /**
     * Récupère tous les magasins
     * @return Liste de tous les magasins
     */
    public List<Store> getAllStores() {
        return storeDAO.findAll();
    }

    /**
     * Récupère les magasins accessibles à l'utilisateur courant
     * @return Liste des magasins accessibles
     */
    public List<Store> getAccessibleStores() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }

        // Les admins ont accès à tous les magasins
        if (currentUser.isAdmin()) {
            return storeDAO.findAll();
        }

        // Les employés n'ont accès qu'aux magasins auxquels ils sont assignés
        return storeAccessDAO.getAccessibleStores(currentUser.getId());
    }

    /**
     * Récupère un magasin par son ID
     * @param id L'ID du magasin
     * @return Le magasin ou null
     */
    public Store getStoreById(int id) {
        Optional<Store> storeOpt = storeDAO.findById(id);
        return storeOpt.orElse(null);
    }

    /**
     * Vérifie si l'utilisateur courant a accès à un magasin
     * @param storeId L'ID du magasin
     * @return true si accès autorisé
     */
    public boolean hasAccess(int storeId) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // Les admins ont accès à tout
        if (currentUser.isAdmin()) {
            return true;
        }

        return storeAccessDAO.hasAccess(currentUser.getId(), storeId);
    }

    /**
     * Supprime un magasin (admin uniquement)
     * @param storeId L'ID du magasin à supprimer
     * @return Le résultat de l'opération
     */
    public ServiceResult deleteStore(int storeId) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut supprimer un magasin");
        }

        // Supprimer d'abord les articles du magasin
        itemDAO.deleteByStoreId(storeId);

        // Supprimer les accès au magasin
        storeAccessDAO.removeAllAccessForStore(storeId);

        // Supprimer le magasin
        if (storeDAO.delete(storeId)) {
            return new ServiceResult(true, "Magasin supprimé avec succès");
        }

        return new ServiceResult(false, "Erreur lors de la suppression du magasin");
    }

    /**
     * Ajoute un employé à un magasin (admin uniquement)
     * @param userId L'ID de l'utilisateur
     * @param storeId L'ID du magasin
     * @return Le résultat de l'opération
     */
    public ServiceResult addEmployeeToStore(int userId, int storeId) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut ajouter des employés");
        }

        if (storeAccessDAO.addAccess(userId, storeId)) {
            return new ServiceResult(true, "Employé ajouté au magasin avec succès");
        }

        return new ServiceResult(false, "Erreur lors de l'ajout de l'employé");
    }

    /**
     * Retire un employé d'un magasin (admin uniquement)
     * @param userId L'ID de l'utilisateur
     * @param storeId L'ID du magasin
     * @return Le résultat de l'opération
     */
    public ServiceResult removeEmployeeFromStore(int userId, int storeId) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut retirer des employés");
        }

        if (storeAccessDAO.removeAccess(userId, storeId)) {
            return new ServiceResult(true, "Employé retiré du magasin avec succès");
        }

        return new ServiceResult(false, "Erreur lors du retrait de l'employé");
    }

    /**
     * Récupère les employés ayant accès à un magasin
     * @param storeId L'ID du magasin
     * @return Liste des utilisateurs avec accès
     */
    public List<User> getStoreEmployees(int storeId) {
        List<User> users = storeAccessDAO.getUsersWithAccess(storeId);
        // Masquer les mots de passe
        for (User user : users) {
            user.setPassword("[PROTECTED]");
        }
        return users;
    }
}
