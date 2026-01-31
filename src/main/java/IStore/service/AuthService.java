package IStore.service;

import IStore.dao.UserDAO;
import IStore.dao.WhitelistDAO;
import IStore.model.Role;
import IStore.model.User;
import IStore.util.PasswordUtil;
import IStore.util.SessionManager;
import IStore.util.ValidationUtil;

import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;
    private final WhitelistDAO whitelistDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.whitelistDAO = new WhitelistDAO();
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }

    public AuthResult login(String email, String password) {
        String emailError = ValidationUtil.validateEmail(email);
        if (emailError != null) {
            return new AuthResult(false, emailError, null);
        }

        if (ValidationUtil.isEmpty(password)) {
            return new AuthResult(false, "Le mot de passe est requis", null);
        }

        Optional<User> userOpt = userDAO.findByEmail(email.trim());
        if (userOpt.isEmpty()) {
            return new AuthResult(false, "Email ou mot de passe incorrect", null);
        }

        User user = userOpt.get();

        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            return new AuthResult(false, "Email ou mot de passe incorrect", null);
        }

        SessionManager.setCurrentUser(user);
        return new AuthResult(true, "Connexion réussie", user);
    }

    public AuthResult register(String email, String pseudo, String password, String confirmPassword) {
        String emailError = ValidationUtil.validateEmail(email);
        if (emailError != null) {
            return new AuthResult(false, emailError, null);
        }

        String pseudoError = ValidationUtil.validatePseudo(pseudo);
        if (pseudoError != null) {
            return new AuthResult(false, pseudoError, null);
        }

        String passwordError = ValidationUtil.validatePassword(password);
        if (passwordError != null) {
            return new AuthResult(false, passwordError, null);
        }

        if (!password.equals(confirmPassword)) {
            return new AuthResult(false, "Les mots de passe ne correspondent pas", null);
        }

        String normalizedEmail = email.toLowerCase().trim();

        if (userDAO.emailExists(normalizedEmail)) {
            return new AuthResult(false, "Un compte existe déjà avec cet email", null);
        }

        boolean isFirstUser = userDAO.count() == 0;

        if (!isFirstUser && !whitelistDAO.isWhitelisted(normalizedEmail)) {
            return new AuthResult(false, "Votre email n'est pas autorisé à créer un compte. Contactez un administrateur.", null);
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        Role role = isFirstUser ? Role.ADMIN : Role.EMPLOYEE;
        User newUser = new User(normalizedEmail, pseudo.trim(), hashedPassword, role);

        User createdUser = userDAO.create(newUser);
        if (createdUser == null) {
            return new AuthResult(false, "Erreur lors de la création du compte", null);
        }

        if (!isFirstUser) {
            whitelistDAO.deleteByEmail(normalizedEmail);
        }

        return new AuthResult(true,
            isFirstUser ? "Compte administrateur créé avec succès" : "Compte créé avec succès",
            createdUser);
    }

    public void logout() {
        SessionManager.logout();
    }

    public boolean isLoggedIn() {
        return SessionManager.isLoggedIn();
    }

    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }
}
