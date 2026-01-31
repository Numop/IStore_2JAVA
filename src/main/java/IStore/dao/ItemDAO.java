package IStore.dao;

import IStore.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemDAO {
    private final Connection connection;

    public ItemDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

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
