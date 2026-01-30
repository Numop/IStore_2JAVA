package IStore.model;

/**
 * Représente un magasin dans le système.
 * Chaque magasin possède un inventaire unique.
 *
 * @author IStore Team
 * @version 1.0
 */
public class Store {
    private int id;
    private String name;

    /**
     * Constructeur par défaut
     */
    public Store() {}

    /**
     * Constructeur complet
     *
     * @param id L'identifiant unique du magasin
     * @param name Le nom du magasin
     */
    public Store(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructeur sans id (pour création)
     *
     * @param name Le nom du magasin
     */
    public Store(String name) {
        this.name = name;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }
}
