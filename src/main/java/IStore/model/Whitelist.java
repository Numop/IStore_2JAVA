package IStore.model;

/**
 * Représente un email autorisé à créer un compte.
 * Seuls les emails whitelistés peuvent s'inscrire.
 *
 * @author IStore Team
 * @version 1.0
 */
public class Whitelist {
    private int id;
    private String email;

    /**
     * Constructeur par défaut
     */
    public Whitelist() {}

    /**
     * Constructeur complet
     *
     * @param id L'identifiant unique
     * @param email L'email autorisé
     */
    public Whitelist(int id, String email) {
        this.id = id;
        this.email = email;
    }

    /**
     * Constructeur sans id (pour création)
     *
     * @param email L'email à autoriser
     */
    public Whitelist(String email) {
        this.email = email;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return email;
    }
}
