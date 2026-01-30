package IStore.service;

import IStore.dao.UserDAO;
import IStore.dao.StoreAccessDAO;
import IStore.model.User;
import IStore.model.Role;
import IStore.util.PasswordUtil;
import IStore.util.SessionManager;
import IStore.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des utilisateurs.
 * Gère les opérations CRUD sur les utilisateurs avec contrôle d'accès.
 *
 * @author IStore Team
 * @version 1.0
 */
public class UserService {
    private final UserDAO userDAO;
    private final StoreAccessDAO storeAccessDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.storeAccessDAO = new StoreAccessDAO();
    }

    /**
     * Résultat d'une opération
     */
    public static class ServiceResult {
        private final boolean success;
        private final String message;

        public ServiceResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    /**
     * Récupère tous les utilisateurs (sans les mots de passe pour les non-admin)
     * @return Liste des utilisateurs
     */
    public List<User> getAllUsers() {
        List<User> users = userDAO.findAll();
        // Masquer les mots de passe pour la sécurité
        for (User user : users) {
            user.setPassword("[PROTECTED]");
        }
        return users;
    }

    /**
     * Récupère un utilisateur par son ID
     * @param id L'ID de l'utilisateur
     * @return L'utilisateur ou null
     */
    public User getUserById(int id) {
        Optional<User> userOpt = userDAO.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword("[PROTECTED]");
            return user;
        }
        return null;
    }

    /**
     * Met à jour un utilisateur
     * @param userId L'ID de l'utilisateur à modifier
     * @param newPseudo Le nouveau pseudo
     * @param newEmail Le nouvel email
     * @param newPassword Le nouveau mot de passe (vide = pas de changement)
     * @return Le résultat de l'opération
     */
    public ServiceResult updateUser(int userId, String newPseudo, String newEmail, String newPassword) {
        User currentUser = SessionManager.getCurrentUser();

        // Vérification des permissions
        if (currentUser == null) {
            return new ServiceResult(false, "Vous devez être connecté");
        }

        // Seul l'utilisateur lui-même ou un admin peut modifier
        if (currentUser.getId() != userId && !currentUser.isAdmin()) {
            return new ServiceResult(false, "Vous n'avez pas la permission de modifier cet utilisateur");
        }

        // Récupérer l'utilisateur à modifier
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            return new ServiceResult(false, "Utilisateur non trouvé");
        }

        User userToUpdate = userOpt.get();

        // Validation du pseudo
        String pseudoError = ValidationUtil.validatePseudo(newPseudo);
        if (pseudoError != null) {
            return new ServiceResult(false, pseudoError);
        }

        // Validation de l'email
        String emailError = ValidationUtil.validateEmail(newEmail);
        if (emailError != null) {
            return new ServiceResult(false, emailError);
        }

        // Vérifier si le nouvel email est déjà utilisé par un autre utilisateur
        Optional<User> existingUser = userDAO.findByEmail(newEmail);
        if (existingUser.isPresent() && existingUser.get().getId() != userId) {
            return new ServiceResult(false, "Cet email est déjà utilisé par un autre utilisateur");
        }

        // Mise à jour des champs
        userToUpdate.setPseudo(newPseudo.trim());
        userToUpdate.setEmail(newEmail.toLowerCase().trim());

        // Mise à jour du mot de passe si fourni
        if (!ValidationUtil.isEmpty(newPassword)) {
            String passwordError = ValidationUtil.validatePassword(newPassword);
            if (passwordError != null) {
                return new ServiceResult(false, passwordError);
            }
            userToUpdate.setPassword(PasswordUtil.hashPassword(newPassword));
        }

        // Sauvegarder
        if (userDAO.update(userToUpdate)) {
            // Mettre à jour la session si c'est l'utilisateur courant
            if (currentUser.getId() == userId) {
                SessionManager.setCurrentUser(userToUpdate);
            }
            return new ServiceResult(true, "Utilisateur mis à jour avec succès");
        }

        return new ServiceResult(false, "Erreur lors de la mise à jour");
    }

    /**
     * Met à jour le rôle d'un utilisateur (admin uniquement)
     * @param userId L'ID de l'utilisateur
     * @param newRole Le nouveau rôle
     * @return Le résultat de l'opération
     */
    public ServiceResult updateUserRole(int userId, Role newRole) {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null || !currentUser.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut modifier les rôles");
        }

        if (currentUser.getId() == userId) {
            return new ServiceResult(false, "Vous ne pouvez pas modifier votre propre rôle");
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            return new ServiceResult(false, "Utilisateur non trouvé");
        }

        User user = userOpt.get();
        user.setRole(newRole);

        if (userDAO.update(user)) {
            return new ServiceResult(true, "Rôle mis à jour avec succès");
        }

        return new ServiceResult(false, "Erreur lors de la mise à jour du rôle");
    }

    /**
     * Supprime un utilisateur
     * @param userId L'ID de l'utilisateur à supprimer
     * @return Le résultat de l'opération
     */
    public ServiceResult deleteUser(int userId) {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            return new ServiceResult(false, "Vous devez être connecté");
        }

        // Seul l'utilisateur lui-même ou un admin peut supprimer
        if (currentUser.getId() != userId && !currentUser.isAdmin()) {
            return new ServiceResult(false, "Vous n'avez pas la permission de supprimer cet utilisateur");
        }

        // Supprimer les accès aux magasins
        storeAccessDAO.removeAllAccessForUser(userId);

        // Supprimer l'utilisateur
        if (userDAO.delete(userId)) {
            // Si l'utilisateur supprime son propre compte, déconnecter
            if (currentUser.getId() == userId) {
                SessionManager.logout();
            }
            return new ServiceResult(true, "Utilisateur supprimé avec succès");
        }

        return new ServiceResult(false, "Erreur lors de la suppression");
    }
}
