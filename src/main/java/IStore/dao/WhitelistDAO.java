package IStore.dao;

import IStore.model.Whitelist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WhitelistDAO {
    private final Connection connection;

    public WhitelistDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public Whitelist create(Whitelist whitelist) {
        String sql = "INSERT INTO whitelist (email) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, whitelist.getEmail().toLowerCase().trim());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    whitelist.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout à la whitelist: " + e.getMessage());
            return null;
        }
        return whitelist;
    }

    public Optional<Whitelist> findById(int id) {
        String sql = "SELECT * FROM whitelist WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Whitelist(rs.getInt("id"), rs.getString("email")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean isWhitelisted(String email) {
        String sql = "SELECT COUNT(*) FROM whitelist WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email.toLowerCase().trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification: " + e.getMessage());
        }
        return false;
    }

    public List<Whitelist> findAll() {
        List<Whitelist> whitelists = new ArrayList<>();
        String sql = "SELECT * FROM whitelist ORDER BY email";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                whitelists.add(new Whitelist(rs.getInt("id"), rs.getString("email")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
        }
        return whitelists;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM whitelist WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteByEmail(String email) {
        String sql = "DELETE FROM whitelist WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email.toLowerCase().trim());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            return false;
        }
    }
}
