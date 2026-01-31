package IStore.model;

public class User {
    private int id;
    private String email;
    private String pseudo;
    private String password;
    private Role role;

    public User() {}

    public User(int id, String email, String pseudo, String password, Role role) {
        this.id = id;
        this.email = email;
        this.pseudo = pseudo;
        this.password = password;
        this.role = role;
    }

    public User(String email, String pseudo, String password, Role role) {
        this.email = email;
        this.pseudo = pseudo;
        this.password = password;
        this.role = role;
    }

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

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', pseudo='" + pseudo + "', role=" + role + "}";
    }
}
