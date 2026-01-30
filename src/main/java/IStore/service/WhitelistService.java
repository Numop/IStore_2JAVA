package IStore.service;

import IStore.dao.WhitelistDAO;
import IStore.dao.UserDAO;
import IStore.model.Whitelist;
import IStore.util.SessionManager;
import IStore.util.ValidationUtil;

import java.util.List;

/**
 * Service de gestion de la whitelist.
 * Permet aux administrateurs de gérer les emails autorisés à s'inscrire.
 *
 * @author IStore Team
 * @version 1.0
 */
public class WhitelistService {
    private final WhitelistDAO whitelistDAO;
    private final UserDAO userDAO;

    public WhitelistService() {
        this.whitelistDAO = new WhitelistDAO();
        this.userDAO = new UserDAO();
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
     * Ajoute un email à la whitelist (admin uniquement)
     * @param email L'email à ajouter
     * @return Le résultat de l'opération
     */
    public ServiceResult addEmail(String email) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut modifier la whitelist");
        }

        String emailError = ValidationUtil.validateEmail(email);
        if (emailError != null) {
            return new ServiceResult(false, emailError);
        }

        String normalizedEmail = email.toLowerCase().trim();

        // Vérifier si l'email est déjà dans la whitelist
        if (whitelistDAO.isWhitelisted(normalizedEmail)) {
            return new ServiceResult(false, "Cet email est déjà dans la whitelist");
        }

        // Vérifier si un compte existe déjà avec cet email
        if (userDAO.emailExists(normalizedEmail)) {
            return new ServiceResult(false, "Un compte existe déjà avec cet email");
        }

        Whitelist whitelist = new Whitelist(normalizedEmail);
        if (whitelistDAO.create(whitelist) != null) {
            return new ServiceResult(true, "Email ajouté à la whitelist");
        }

        return new ServiceResult(false, "Erreur lors de l'ajout à la whitelist");
    }

    /**
     * Supprime un email de la whitelist (admin uniquement)
     * @param id L'ID de l'entrée à supprimer
     * @return Le résultat de l'opération
     */
    public ServiceResult removeEmail(int id) {
        if (!SessionManager.isAdmin()) {
            return new ServiceResult(false, "Seul un administrateur peut modifier la whitelist");
        }

        if (whitelistDAO.delete(id)) {
            return new ServiceResult(true, "Email retiré de la whitelist");
        }

        return new ServiceResult(false, "Erreur lors de la suppression");
    }

    /**
     * Récupère tous les emails de la whitelist
     * @return Liste des entrées whitelist
     */
    public List<Whitelist> getAllWhitelistedEmails() {
        return whitelistDAO.findAll();
    }

    /**
     * Vérifie si un email est whitelisté
     * @param email L'email à vérifier
     * @return true si whitelisté
     */
    public boolean isWhitelisted(String email) {
        return whitelistDAO.isWhitelisted(email);
    }
}
