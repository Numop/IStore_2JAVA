package IStore.model;
}
    }
        return "User{id=" + id + ", email='" + email + "', pseudo='" + pseudo + "', role=" + role + "}";
    public String toString() {
    @Override

    }
        return role == Role.ADMIN;
    public boolean isAdmin() {
     */
     * @return true si admin
     * Vérifie si l'utilisateur est administrateur
    /**

    public void setRole(Role role) { this.role = role; }
    public Role getRole() { return role; }

    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return password; }

    public void setPseudo(String pseudo) { this.pseudo = pseudo; }
    public String getPseudo() { return pseudo; }

    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    // Getters et Setters

    }
        this.role = role;
        this.password = password;
        this.pseudo = pseudo;
        this.email = email;
    public User(String email, String pseudo, String password, Role role) {
     */
     * Constructeur sans id (pour création)
    /**

    }
        this.role = role;
        this.password = password;
        this.pseudo = pseudo;
        this.email = email;
        this.id = id;
    public User(int id, String email, String pseudo, String password, Role role) {
     */
     * @param role Le rôle (ADMIN ou EMPLOYEE)
     * @param password Le mot de passe hashé
     * @param pseudo Le pseudo affiché
     * @param email L'email de l'utilisateur (login)
     * @param id L'identifiant unique
     *
     * Constructeur complet
    /**

    public User() {}
     */
     * Constructeur par défaut
    /**

    private Role role;
    private String password; // Stocké hashé avec BCrypt
    private String pseudo;
    private String email;
    private int id;
public class User {
 */
 * @version 1.0
 * @author IStore Team
 *
 * Contient les informations d'identification et le rôle.
 * Représente un utilisateur du système.
/**

