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

public class UserService {
    private final UserDAO userDAO;
    private final StoreAccessDAO storeAccessDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.storeAccessDAO = new StoreAccessDAO();
    }

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

    public List<User> getAllUsers() {
        List<User> users = userDAO.findAll();
        for (User user : users) {
            user.setPassword("[PROTECTED]");
        }
        return users;
    }

    public User getUserById(int id) {
        Optional<User> userOpt = userDAO.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword("[PROTECTED]");
            return user;
        }
        return null;
    }

    public ServiceResult updateUser(int userId, String newPseudo, String newEmail, String newPassword) {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            return new ServiceResult(false, "Vous devez être connecté");
        }

        if (currentUser.getId() != userId && !currentUser.isAdmin()) {
            return new ServiceResult(false, "Vous n'avez pas la permission de modifier cet utilisateur");
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            return new ServiceResult(false, "Utilisateur non trouvé");
        }

        User userToUpdate = userOpt.get();

        String pseudoError = ValidationUtil.validatePseudo(newPseudo);
        if (pseudoError != null) {
            return new ServiceResult(false, pseudoError);
        }

        String emailError = ValidationUtil.validateEmail(newEmail);
        if (emailError != null) {
            return new ServiceResult(false, emailError);
        }

        Optional<User> existingUser = userDAO.findByEmail(newEmail);
        if (existingUser.isPresent() && existingUser.get().getId() != userId) {
            return new ServiceResult(false, "Cet email est déjà utilisé par un autre utilisateur");
        }

        userToUpdate.setPseudo(newPseudo.trim());
        userToUpdate.setEmail(newEmail.toLowerCase().trim());

        if (!ValidationUtil.isEmpty(newPassword)) {
            String passwordError = ValidationUtil.validatePassword(newPassword);
            if (passwordError != null) {
                return new ServiceResult(false, passwordError);
            }
            userToUpdate.setPassword(PasswordUtil.hashPassword(newPassword));
        }

        if (userDAO.update(userToUpdate)) {
            if (currentUser.getId() == userId) {
                SessionManager.setCurrentUser(userToUpdate);
            }
            return new ServiceResult(true, "Utilisateur mis à jour avec succès");
        }

        return new ServiceResult(false, "Erreur lors de la mise à jour");
    }

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

    public ServiceResult deleteUser(int userId) {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            return new ServiceResult(false, "Vous devez être connecté");
        }

        if (currentUser.getId() != userId && !currentUser.isAdmin()) {
            return new ServiceResult(false, "Vous n'avez pas la permission de supprimer cet utilisateur");
        }

        storeAccessDAO.removeAllAccessForUser(userId);

        if (userDAO.delete(userId)) {
            if (currentUser.getId() == userId) {
                SessionManager.logout();
            }
            return new ServiceResult(true, "Utilisateur supprimé avec succès");
        }

        return new ServiceResult(false, "Erreur lors de la suppression");
    }
}
