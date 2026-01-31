package IStore.dao;

import IStore.model.Store;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StoreDAO {
    private final Connection connection;

    public StoreDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public Store create(Store store) {
        String sql = "INSERT INTO stores (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, store.getName().trim());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    store.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du magasin: " + e.getMessage());
            return null;
        }
        return store;
    }

    public Optional<Store> findById(int id) {
        String sql = "SELECT * FROM stores WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Store(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du magasin: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Store> findByName(String name) {
        String sql = "SELECT * FROM stores WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name.trim().toLowerCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Store(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par nom: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Store> findAll() {
        List<Store> stores = new ArrayList<>();
        String sql = "SELECT * FROM stores ORDER BY name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stores.add(new Store(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des magasins: " + e.getMessage());
        }
        return stores;
    }

    public boolean update(Store store) {
        String sql = "UPDATE stores SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, store.getName().trim());
            pstmt.setInt(2, store.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du magasin: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM stores WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du magasin: " + e.getMessage());
            return false;
        }
    }

    public boolean nameExists(String name) {
        return findByName(name).isPresent();
    }
}
