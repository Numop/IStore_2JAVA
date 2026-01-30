package IStore.dao;
}
    }
        );
            Role.valueOf(rs.getString("role"))
            rs.getString("password"),
            rs.getString("pseudo"),
            rs.getString("email"),
            rs.getInt("id"),
        return new User(
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
     */
     * Convertit un ResultSet en objet User
    /**

    }
        return findByEmail(email).isPresent();
    public boolean emailExists(String email) {
     */
     * @return true si l'email existe
     * @param email L'email à vérifier
     * Vérifie si un email existe déjà
    /**

    }
        return 0;
        }
            System.err.println("Erreur lors du comptage des utilisateurs: " + e.getMessage());
        } catch (SQLException e) {
            }
                return rs.getInt(1);
            if (rs.next()) {
             ResultSet rs = stmt.executeQuery(sql)) {
        try (Statement stmt = connection.createStatement();
        String sql = "SELECT COUNT(*) FROM users";
    public int count() {
     */
     * @return Le nombre d'utilisateurs
     * Compte le nombre d'utilisateurs
    /**

    }
        }
            return false;
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
        } catch (SQLException e) {
            return pstmt.executeUpdate() > 0;
            pstmt.setInt(1, id);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        String sql = "DELETE FROM users WHERE id = ?";
    public boolean delete(int id) {
     */
     * @return true si succès
     * @param id L'ID de l'utilisateur à supprimer
     * Supprime un utilisateur
    /**

    }
        }
            return false;
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        } catch (SQLException e) {
            return pstmt.executeUpdate() > 0;
            pstmt.setInt(5, user.getId());
            pstmt.setString(4, user.getRole().name());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(2, user.getPseudo());
            pstmt.setString(1, user.getEmail().toLowerCase().trim());
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        String sql = "UPDATE users SET email = ?, pseudo = ?, password = ?, role = ? WHERE id = ?";
    public boolean update(User user) {
     */
     * @return true si succès
     * @param user L'utilisateur à mettre à jour
     * Met à jour un utilisateur
    /**

    }
        return users;
        }
            System.err.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        } catch (SQLException e) {
            }
                users.add(mapResultSetToUser(rs));
            while (rs.next()) {
             ResultSet rs = stmt.executeQuery(sql)) {
        try (Statement stmt = connection.createStatement();
        String sql = "SELECT * FROM users ORDER BY id";
        List<User> users = new ArrayList<>();
    public List<User> findAll() {
     */
     * @return Liste de tous les utilisateurs
     * Récupère tous les utilisateurs
    /**

    }
        return Optional.empty();
        }
            System.err.println("Erreur lors de la recherche par email: " + e.getMessage());
        } catch (SQLException e) {
            }
                }
                    return Optional.of(mapResultSetToUser(rs));
                if (rs.next()) {
            try (ResultSet rs = pstmt.executeQuery()) {
            pstmt.setString(1, email.toLowerCase().trim());
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
    public Optional<User> findByEmail(String email) {
     */
     * @return Optional contenant l'utilisateur ou vide
     * @param email L'email de l'utilisateur
     * Trouve un utilisateur par son email
    /**

    }
        return Optional.empty();
        }
            System.err.println("Erreur lors de la recherche de l'utilisateur: " + e.getMessage());
        } catch (SQLException e) {
            }
                }
                    return Optional.of(mapResultSetToUser(rs));
                if (rs.next()) {
            try (ResultSet rs = pstmt.executeQuery()) {
            pstmt.setInt(1, id);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        String sql = "SELECT * FROM users WHERE id = ?";
    public Optional<User> findById(int id) {
     */
     * @return Optional contenant l'utilisateur ou vide
     * @param id L'ID de l'utilisateur
     * Trouve un utilisateur par son ID
    /**

    }
        return user;
        }
            return null;
            System.err.println("Erreur lors de la création de l'utilisateur: " + e.getMessage());
        } catch (SQLException e) {
            }
                }
                    user.setId(generatedKeys.getInt(1));
                if (generatedKeys.next()) {
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {

            pstmt.executeUpdate();
            pstmt.setString(4, user.getRole().name());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(2, user.getPseudo());
            pstmt.setString(1, user.getEmail().toLowerCase().trim());
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        String sql = "INSERT INTO users (email, pseudo, password, role) VALUES (?, ?, ?, ?)";
    public User create(User user) {
     */
     * @return L'utilisateur créé avec son ID
     * @param user L'utilisateur à créer
     * Crée un nouvel utilisateur
    /**

    }
        this.connection = DatabaseManager.getInstance().getConnection();
    public UserDAO() {

    private final Connection connection;
public class UserDAO {
 */
 * @version 1.0
 * @author IStore Team
 *
 * DAO pour la gestion des utilisateurs en base de données.
/**

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

import IStore.model.User;
import IStore.model.Role;

