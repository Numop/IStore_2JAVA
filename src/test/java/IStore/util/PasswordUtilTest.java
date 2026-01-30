package IStore.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour les utilitaires de mot de passe.
 *
 * @author IStore Team
 * @version 1.0
 */
public class PasswordUtilTest {

    @Test
    @DisplayName("Le hashage produit un hash différent du mot de passe")
    void testHashIsDifferent() {
        String password = "mySecurePassword123";
        String hash = PasswordUtil.hashPassword(password);

        assertNotEquals(password, hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"));
    }

    @Test
    @DisplayName("La vérification réussit avec le bon mot de passe")
    void testVerifyCorrectPassword() {
        String password = "mySecurePassword123";
        String hash = PasswordUtil.hashPassword(password);

        assertTrue(PasswordUtil.verifyPassword(password, hash));
    }

    @Test
    @DisplayName("La vérification échoue avec un mauvais mot de passe")
    void testVerifyWrongPassword() {
        String password = "mySecurePassword123";
        String wrongPassword = "wrongPassword";
        String hash = PasswordUtil.hashPassword(password);

        assertFalse(PasswordUtil.verifyPassword(wrongPassword, hash));
    }

    @Test
    @DisplayName("Deux hashages du même mot de passe sont différents (salt)")
    void testDifferentHashes() {
        String password = "samePassword";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        assertNotEquals(hash1, hash2, "Les hashs doivent être différents grâce au salt");

        // Mais les deux doivent valider le même mot de passe
        assertTrue(PasswordUtil.verifyPassword(password, hash1));
        assertTrue(PasswordUtil.verifyPassword(password, hash2));
    }

    @Test
    @DisplayName("La vérification avec un hash invalide retourne false")
    void testVerifyInvalidHash() {
        assertFalse(PasswordUtil.verifyPassword("password", "invalidhash"));
    }
}
