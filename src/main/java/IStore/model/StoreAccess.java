package IStore.model;

/**
 * Représente l'accès d'un employé à un magasin.
 * Table de jonction entre User et Store.
 *
 * @author IStore Team
 * @version 1.0
 */
public class StoreAccess {
    private int userId;
    private int storeId;

    /**
     * Constructeur par défaut
     */
    public StoreAccess() {}

    /**
     * Constructeur complet
     *
     * @param userId L'identifiant de l'utilisateur
     * @param storeId L'identifiant du magasin
     */
    public StoreAccess(int userId, int storeId) {
        this.userId = userId;
        this.storeId = storeId;
    }

    // Getters et Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    @Override
    public String toString() {
        return "StoreAccess{userId=" + userId + ", storeId=" + storeId + "}";
    }
}
