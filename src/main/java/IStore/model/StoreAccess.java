package IStore.model;

public class StoreAccess {
    private int userId;
    private int storeId;

    public StoreAccess() {}

    public StoreAccess(int userId, int storeId) {
        this.userId = userId;
        this.storeId = storeId;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    @Override
    public String toString() {
        return "StoreAccess{userId=" + userId + ", storeId=" + storeId + "}";
    }
}
