package IStore.dao;

import IStore.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des articles en base de données.
 *
 * @author IStore Team
 * @version 1.0
 */
public class ItemDAO {
    private final Connection connection;

    public ItemDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Crée un nouvel article
     * @param item L'article à créer
     * @return L'article créé avec son ID
     */
    public Item create(Item item) {
        String sql = "INSERT INTO items (name, price, quantity, store_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, item.getName().trim());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setInt(3, Math.max(0, item.getQuantity()));
            pstmt.setInt(4, item.getStoreId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de l'article: " + e.getMessage());
            return null;
        }
        return item;
    }

    /**
     * Trouve un article par son ID
     * @param id L'ID de l'article
     * @return Optional contenant l'article ou vide
     */
    public Optional<Item> findById(int id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'article: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Récupère tous les articles d'un magasin
     * @param storeId L'ID du magasin
     * @return Liste des articles du magasin
     */
    public List<Item> findByStoreId(int storeId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE store_id = ? ORDER BY name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles: " + e.getMessage());
        }
        return items;
    }

    /**
     * Récupère tous les articles
     * @return Liste de tous les articles
     */
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles: " + e.getMessage());
        }
        return items;
    }

    /**
     * Met à jour un article
     * @param item L'article à mettre à jour
     * @return true si succès
     */
    public boolean update(Item item) {
        String sql = "UPDATE items SET name = ?, price = ?, quantity = ?, store_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, item.getName().trim());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setInt(3, Math.max(0, item.getQuantity()));
            pstmt.setInt(4, item.getStoreId());
            pstmt.setInt(5, item.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'article: " + e.getMessage());
            return false;
        }
    }

    /**
     * Met à jour uniquement la quantité d'un article
     * @param id L'ID de l'article
     * @param newQuantity La nouvelle quantité
     * @return true si succès
     */
    public boolean updateQuantity(int id, int newQuantity) {
        String sql = "UPDATE items SET quantity = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, Math.max(0, newQuantity));
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la quantité: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un article
     * @param id L'ID de l'article à supprimer
     * @return true si succès
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'article: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime tous les articles d'un magasin
     * @param storeId L'ID du magasin
     * @return true si succès
     */
    public boolean deleteByStoreId(int storeId) {
        String sql = "DELETE FROM items WHERE store_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des articles: " + e.getMessage());
            return false;
        }
    }

    /**
     * Convertit un ResultSet en objet Item
     */
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        return new Item(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getDouble("price"),
            rs.getInt("quantity"),
            rs.getInt("store_id")
        );
    }
}
