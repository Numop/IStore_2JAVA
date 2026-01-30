package IStore.util;

import IStore.model.User;

/**
 * Gestionnaire de session utilisateur.
 * Stocke l'utilisateur actuellement connecté.
 *
 * @author IStore Team
 * @version 1.0
 */
public class SessionManager {

    private static User currentUser = null;

    /**
     * Définit l'utilisateur connecté
     * @param user L'utilisateur connecté
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Récupère l'utilisateur connecté
     * @return L'utilisateur connecté ou null
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Vérifie si un utilisateur est connecté
     * @return true si un utilisateur est connecté
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Vérifie si l'utilisateur connecté est admin
     * @return true si admin
     */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Déconnecte l'utilisateur
     */
    public static void logout() {
        currentUser = null;
    }

    /**
     * Récupère l'ID de l'utilisateur connecté
     * @return L'ID ou -1 si non connecté
     */
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
}
