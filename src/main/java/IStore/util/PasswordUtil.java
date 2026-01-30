package IStore.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaires pour le hashage et la vérification des mots de passe.
 * Utilise BCrypt pour un stockage sécurisé.
 *
 * @author IStore Team
 * @version 1.0
 */
public class PasswordUtil {

    // Coût du hashage (plus élevé = plus sécurisé mais plus lent)
    private static final int BCRYPT_ROUNDS = 12;

    /**
     * Hash un mot de passe avec BCrypt
     * @param password Le mot de passe en clair
     * @return Le mot de passe hashé
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Vérifie si un mot de passe correspond au hash
     * @param password Le mot de passe en clair à vérifier
     * @param hashedPassword Le hash stocké
     * @return true si le mot de passe correspond
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
