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

public class StoreService {
    private final StoreDAO storeDAO;
    private final StoreAccessDAO storeAccessDAO;
    private final ItemDAO itemDAO;

    public StoreService() {
        this.storeDAO = new StoreDAO();
        this.storeAccessDAO = new StoreAccessDAO();
        this.itemDAO = new ItemDAO();
    }

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

    public List<Store> getAllStores() {
        return storeDAO.findAll();
    }

    public List<Store> getAccessibleStores() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }

        if (currentUser.isAdmin()) {
            return storeDAO.findAll();
        }

        return storeAccessDAO.getAccessibleStores(currentUser.getId());
    }

    public Store getStoreById(int id) {
        Optional<Store> storeOpt = storeDAO.findById(id);
        return storeOpt.orElse(null);
    }

    public boolean hasAccess(int storeId) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return storeAccessDAO.hasAccess(currentUser.getId(), storeId);
    }

    public ServiceResult deleteStore(int storeId) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut supprimer un magasin");
        }

        itemDAO.deleteByStoreId(storeId);
        storeAccessDAO.removeAllAccessForStore(storeId);

        if (storeDAO.delete(storeId)) {
            return new ServiceResult(true, "Magasin supprimé avec succès");
        }

        return new ServiceResult(false, "Erreur lors de la suppression du magasin");
    }

    public ServiceResult addEmployeeToStore(int userId, int storeId) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut ajouter des employés");
        }

        if (storeAccessDAO.addAccess(userId, storeId)) {
            return new ServiceResult(true, "Employé ajouté au magasin avec succès");
        }

        return new ServiceResult(false, "Erreur lors de l'ajout de l'employé");
    }

    public ServiceResult removeEmployeeFromStore(int userId, int storeId) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut retirer des employés");
        }

        if (storeAccessDAO.removeAccess(userId, storeId)) {
            return new ServiceResult(true, "Employé retiré du magasin avec succès");
        }

        return new ServiceResult(false, "Erreur lors du retrait de l'employé");
    }

    public List<User> getStoreEmployees(int storeId) {
        List<User> users = storeAccessDAO.getUsersWithAccess(storeId);
        for (User user : users) {
            user.setPassword("[PROTECTED]");
        }
        return users;
    }
}
