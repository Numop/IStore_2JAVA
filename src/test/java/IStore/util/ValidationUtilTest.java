package IStore.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour les utilitaires de validation.
 *
 * @author IStore Team
 * @version 1.0
 */
public class ValidationUtilTest {

    // ===== Tests Email =====

    @Test
    @DisplayName("Email valide ne retourne pas d'erreur")
    void testValidEmail() {
        assertNull(ValidationUtil.validateEmail("test@example.com"));
        assertNull(ValidationUtil.validateEmail("user.name@domain.org"));
        assertNull(ValidationUtil.validateEmail("user+tag@domain.co.uk"));
    }

    @Test
    @DisplayName("Email vide retourne une erreur")
    void testEmptyEmail() {
        assertNotNull(ValidationUtil.validateEmail(""));
        assertNotNull(ValidationUtil.validateEmail(null));
        assertNotNull(ValidationUtil.validateEmail("   "));
    }

    @Test
    @DisplayName("Email invalide retourne une erreur")
    void testInvalidEmail() {
        assertNotNull(ValidationUtil.validateEmail("notanemail"));
        assertNotNull(ValidationUtil.validateEmail("missing@domain"));
        assertNotNull(ValidationUtil.validateEmail("@nodomain.com"));
    }

    // ===== Tests Mot de passe =====

    @Test
    @DisplayName("Mot de passe valide ne retourne pas d'erreur")
    void testValidPassword() {
        assertNull(ValidationUtil.validatePassword("password123"));
        assertNull(ValidationUtil.validatePassword("123456"));
    }

    @Test
    @DisplayName("Mot de passe vide retourne une erreur")
    void testEmptyPassword() {
        assertNotNull(ValidationUtil.validatePassword(""));
        assertNotNull(ValidationUtil.validatePassword(null));
    }

    @Test
    @DisplayName("Mot de passe trop court retourne une erreur")
    void testShortPassword() {
        String error = ValidationUtil.validatePassword("12345");
        assertNotNull(error);
        assertTrue(error.contains("6"));
    }

    // ===== Tests Pseudo =====

    @Test
    @DisplayName("Pseudo valide ne retourne pas d'erreur")
    void testValidPseudo() {
        assertNull(ValidationUtil.validatePseudo("JohnDoe"));
        assertNull(ValidationUtil.validatePseudo("ab"));
    }

    @Test
    @DisplayName("Pseudo vide retourne une erreur")
    void testEmptyPseudo() {
        assertNotNull(ValidationUtil.validatePseudo(""));
        assertNotNull(ValidationUtil.validatePseudo(null));
    }

    @Test
    @DisplayName("Pseudo trop court retourne une erreur")
    void testShortPseudo() {
        assertNotNull(ValidationUtil.validatePseudo("a"));
    }

    // ===== Tests Prix =====

    @Test
    @DisplayName("Prix valide ne retourne pas d'erreur")
    void testValidPrice() {
        assertNull(ValidationUtil.validatePrice("10.99"));
        assertNull(ValidationUtil.validatePrice("0"));
        assertNull(ValidationUtil.validatePrice("100"));
    }

    @Test
    @DisplayName("Prix invalide retourne une erreur")
    void testInvalidPrice() {
        assertNotNull(ValidationUtil.validatePrice("abc"));
        assertNotNull(ValidationUtil.validatePrice("-5"));
        assertNotNull(ValidationUtil.validatePrice(""));
    }

    // ===== Tests Quantité =====

    @Test
    @DisplayName("Quantité valide ne retourne pas d'erreur")
    void testValidQuantity() {
        assertNull(ValidationUtil.validateQuantity("10"));
        assertNull(ValidationUtil.validateQuantity("0"));
        assertNull(ValidationUtil.validateQuantity("100"));
    }

    @Test
    @DisplayName("Quantité invalide retourne une erreur")
    void testInvalidQuantity() {
        assertNotNull(ValidationUtil.validateQuantity("abc"));
        assertNotNull(ValidationUtil.validateQuantity("-5"));
        assertNotNull(ValidationUtil.validateQuantity("10.5"));
        assertNotNull(ValidationUtil.validateQuantity(""));
    }

    // ===== Tests isEmpty =====

    @Test
    @DisplayName("isEmpty détecte correctement les chaînes vides")
    void testIsEmpty() {
        assertTrue(ValidationUtil.isEmpty(""));
        assertTrue(ValidationUtil.isEmpty(null));
        assertTrue(ValidationUtil.isEmpty("   "));
        assertFalse(ValidationUtil.isEmpty("text"));
        assertFalse(ValidationUtil.isEmpty(" text "));
    }
}
