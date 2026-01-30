package IStore.model;

/**
 * Représente un article dans l'inventaire d'un magasin.
 *
 * @author IStore Team
 * @version 1.0
 */
public class Item {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int storeId;

    /**
     * Constructeur par défaut
     */
    public Item() {}

    /**
     * Constructeur complet
     *
     * @param id L'identifiant unique
     * @param name Le nom de l'article
     * @param price Le prix unitaire
     * @param quantity La quantité en stock
     * @param storeId L'identifiant du magasin
     */
    public Item(int id, String name, double price, int quantity, int storeId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.storeId = storeId;
    }

    /**
     * Constructeur sans id (pour création)
     */
    public Item(String name, double price, int quantity, int storeId) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.storeId = storeId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }

    /**
     * Définit la quantité (ne peut pas être inférieure à 0)
     * @param quantity La nouvelle quantité
     */
    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    /**
     * Augmente la quantité en stock
     * @param amount La quantité à ajouter
     */
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    /**
     * Diminue la quantité en stock (ne descend pas sous 0)
     * @param amount La quantité à retirer
     * @return true si l'opération a réussi
     */
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
