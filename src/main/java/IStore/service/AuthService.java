package IStore.service;

import IStore.dao.UserDAO;
import IStore.dao.WhitelistDAO;
import IStore.model.Role;
import IStore.model.User;
import IStore.util.PasswordUtil;
import IStore.util.SessionManager;
import IStore.util.ValidationUtil;

import java.util.Optional;

/**
 * Service d'authentification.
 * Gère la connexion, l'inscription et la validation des utilisateurs.
 *
 * @author IStore Team
 * @version 1.0
 */
public class AuthService {
    private final UserDAO userDAO;
    private final WhitelistDAO whitelistDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.whitelistDAO = new WhitelistDAO();
    }

    /**
     * Résultat d'une opération d'authentification
     */
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

    /**
     * Connecte un utilisateur
     * @param email L'email de l'utilisateur
     * @param password Le mot de passe
     * @return Le résultat de l'authentification
     */
    public AuthResult login(String email, String password) {
        // Validation de l'email
        String emailError = ValidationUtil.validateEmail(email);
        if (emailError != null) {
            return new AuthResult(false, emailError, null);
        }

        // Validation du mot de passe
        if (ValidationUtil.isEmpty(password)) {
            return new AuthResult(false, "Le mot de passe est requis", null);
        }

        // Recherche de l'utilisateur
        Optional<User> userOpt = userDAO.findByEmail(email.trim());
        if (userOpt.isEmpty()) {
            return new AuthResult(false, "Email ou mot de passe incorrect", null);
        }

        User user = userOpt.get();

        // Vérification du mot de passe
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            return new AuthResult(false, "Email ou mot de passe incorrect", null);
        }

        // Connexion réussie
        SessionManager.setCurrentUser(user);
        return new AuthResult(true, "Connexion réussie", user);
    }

    /**
     * Inscrit un nouvel utilisateur
     * @param email L'email
     * @param pseudo Le pseudo
     * @param password Le mot de passe
     * @param confirmPassword La confirmation du mot de passe
     * @return Le résultat de l'inscription
     */
    public AuthResult register(String email, String pseudo, String password, String confirmPassword) {
        // Validation de l'email
        String emailError = ValidationUtil.validateEmail(email);
        if (emailError != null) {
            return new AuthResult(false, emailError, null);
        }

        // Validation du pseudo
        String pseudoError = ValidationUtil.validatePseudo(pseudo);
        if (pseudoError != null) {
            return new AuthResult(false, pseudoError, null);
        }

        // Validation du mot de passe
        String passwordError = ValidationUtil.validatePassword(password);
        if (passwordError != null) {
            return new AuthResult(false, passwordError, null);
        }

        // Vérification de la confirmation
        if (!password.equals(confirmPassword)) {
            return new AuthResult(false, "Les mots de passe ne correspondent pas", null);
        }

        String normalizedEmail = email.toLowerCase().trim();

        // Vérification si l'email existe déjà
        if (userDAO.emailExists(normalizedEmail)) {
            return new AuthResult(false, "Un compte existe déjà avec cet email", null);
        }

        // Vérification si c'est le premier utilisateur (sera admin)
        boolean isFirstUser = userDAO.count() == 0;

        // Si ce n'est pas le premier utilisateur, vérifier la whitelist
        if (!isFirstUser && !whitelistDAO.isWhitelisted(normalizedEmail)) {
            return new AuthResult(false, "Votre email n'est pas autorisé à créer un compte. Contactez un administrateur.", null);
        }

        // Hashage du mot de passe
        String hashedPassword = PasswordUtil.hashPassword(password);

        // Création de l'utilisateur
        Role role = isFirstUser ? Role.ADMIN : Role.EMPLOYEE;
        User newUser = new User(normalizedEmail, pseudo.trim(), hashedPassword, role);

        User createdUser = userDAO.create(newUser);
        if (createdUser == null) {
            return new AuthResult(false, "Erreur lors de la création du compte", null);
        }

        // Supprimer l'email de la whitelist après inscription réussie
        if (!isFirstUser) {
            whitelistDAO.deleteByEmail(normalizedEmail);
        }

        return new AuthResult(true,
            isFirstUser ? "Compte administrateur créé avec succès" : "Compte créé avec succès",
            createdUser);
    }

    /**
     * Déconnecte l'utilisateur actuel
     */
    public void logout() {
        SessionManager.logout();
    }

    /**
     * Vérifie si un utilisateur est connecté
     * @return true si connecté
     */
    public boolean isLoggedIn() {
        return SessionManager.isLoggedIn();
    }

    /**
     * Récupère l'utilisateur connecté
     * @return L'utilisateur connecté ou null
     */
    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }
}
