package IStore.model;

public class Item {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int storeId;

    public Item() {}

    public Item(int id, String name, double price, int quantity, int storeId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.storeId = storeId;
    }

    public Item(String name, double price, int quantity, int storeId) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.storeId = storeId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = Math.max(0, quantity); }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    public boolean decreaseQuantity(int amount) {
        if (amount > 0 && this.quantity >= amount) {
            this.quantity -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Item{id=" + id + ", name='" + name + "', price=" + price + ", quantity=" + quantity + "}";
    }
}
