package IStore.model;

/**
 * Represente un utilisateur du systeme.
 * Contient les informations d'identification et le role.
 *
 * @author IStore Team
 * @version 1.0
 */
public class User {
    private int id;
    private String email;
    private String pseudo;
    private String password;
    private Role role;

    /**
     * Constructeur par defaut
     */
    public User() {}

    /**
     * Constructeur complet
     *
     * @param id L'identifiant unique
     * @param email L'email de l'utilisateur (login)
     * @param pseudo Le pseudo affiche
     * @param password Le mot de passe hashe
     * @param role Le role (ADMIN ou EMPLOYEE)
     */
    public User(int id, String email, String pseudo, String password, Role role) {
        this.id = id;
        this.email = email;
        this.pseudo = pseudo;
        this.password = password;
        this.role = role;
    }

    /**
     * Constructeur sans id (pour creation)
     */
    public User(String email, String pseudo, String password, Role role) {
        this.email = email;
        this.pseudo = pseudo;
        this.password = password;
        this.role = role;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    /**
     * Verifie si l'utilisateur est administrateur
     * @return true si admin
     */
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', pseudo='" + pseudo + "', role=" + role + "}";
    }
}
