package IStore.dao;

import IStore.model.Store;
import IStore.model.User;
import IStore.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des accès aux magasins (relation User-Store).
 *
 * @author IStore Team
 * @version 1.0
 */
public class StoreAccessDAO {
    private final Connection connection;

    public StoreAccessDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Ajoute un accès utilisateur à un magasin
     * @param userId L'ID de l'utilisateur
     * @param storeId L'ID du magasin
     * @return true si succès
     */
    public boolean addAccess(int userId, int storeId) {
        String sql = "INSERT OR IGNORE INTO store_access (user_id, store_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, storeId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'accès: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un accès utilisateur à un magasin
     * @param userId L'ID de l'utilisateur
     * @param storeId L'ID du magasin
     * @return true si succès
     */
    public boolean removeAccess(int userId, int storeId) {
        String sql = "DELETE FROM store_access WHERE user_id = ? AND store_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, storeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression d'accès: " + e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si un utilisateur a accès à un magasin
     * @param userId L'ID de l'utilisateur
     * @param storeId L'ID du magasin
     * @return true si l'utilisateur a accès
     */
    public boolean hasAccess(int userId, int storeId) {
        String sql = "SELECT COUNT(*) FROM store_access WHERE user_id = ? AND store_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, storeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification d'accès: " + e.getMessage());
        }
        return false;
    }

    /**
     * Récupère tous les magasins auxquels un utilisateur a accès
     * @param userId L'ID de l'utilisateur
     * @return Liste des magasins accessibles
     */
    public List<Store> getAccessibleStores(int userId) {
        List<Store> stores = new ArrayList<>();
        String sql = """
            SELECT s.id, s.name 
            FROM stores s 
            INNER JOIN store_access sa ON s.id = sa.store_id 
            WHERE sa.user_id = ?
            ORDER BY s.name
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stores.add(new Store(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des magasins: " + e.getMessage());
        }
        return stores;
    }

    /**
     * Récupère tous les utilisateurs ayant accès à un magasin
     * @param storeId L'ID du magasin
     * @return Liste des utilisateurs avec accès
     */
    public List<User> getUsersWithAccess(int storeId) {
        List<User> users = new ArrayList<>();
        String sql = """
            SELECT u.id, u.email, u.pseudo, u.password, u.role 
            FROM users u 
            INNER JOIN store_access sa ON u.id = sa.user_id 
            WHERE sa.store_id = ?
            ORDER BY u.pseudo
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("pseudo"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role"))
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        }
        return users;
    }

    /**
     * Supprime tous les accès d'un utilisateur
     * @param userId L'ID de l'utilisateur
     * @return true si succès
     */
    public boolean removeAllAccessForUser(int userId) {
        String sql = "DELETE FROM store_access WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des accès: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime tous les accès à un magasin
     * @param storeId L'ID du magasin
     * @return true si succès
     */
    public boolean removeAllAccessForStore(int storeId) {
        String sql = "DELETE FROM store_access WHERE store_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des accès: " + e.getMessage());
            return false;
        }
    }
}
