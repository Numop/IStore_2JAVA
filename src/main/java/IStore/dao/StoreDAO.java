package IStore.dao;

import IStore.model.Store;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des magasins en base de données.
 *
 * @author IStore Team
 * @version 1.0
 */
public class StoreDAO {
    private final Connection connection;

    public StoreDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Crée un nouveau magasin
     * @param store Le magasin à créer
     * @return Le magasin créé avec son ID
     */
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

    /**
     * Trouve un magasin par son ID
     * @param id L'ID du magasin
     * @return Optional contenant le magasin ou vide
     */
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

    /**
     * Trouve un magasin par son nom
     * @param name Le nom du magasin
     * @return Optional contenant le magasin ou vide
     */
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

    /**
     * Récupère tous les magasins
     * @return Liste de tous les magasins
     */
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

    /**
     * Met à jour un magasin
     * @param store Le magasin à mettre à jour
     * @return true si succès
     */
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

    /**
     * Supprime un magasin
     * @param id L'ID du magasin à supprimer
     * @return true si succès
     */
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

    /**
     * Vérifie si un nom de magasin existe déjà
     * @param name Le nom à vérifier
     * @return true si le nom existe
     */
    public boolean nameExists(String name) {
        return findByName(name).isPresent();
    }
}
