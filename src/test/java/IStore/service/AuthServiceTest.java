package IStore.service;

import IStore.dao.DatabaseManager;
import IStore.model.Role;
import IStore.model.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le service d'authentification.
 *
 * @author IStore Team
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceTest {

    private static AuthService authService;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_PSEUDO = "TestUser";

    @BeforeAll
    static void setUp() {
        // Initialiser la base de données
        DatabaseManager.getInstance();
        authService = new AuthService();
    }

    @Test
    @Order(1)
    @DisplayName("Le premier utilisateur devient admin automatiquement")
    void testFirstUserIsAdmin() {
        AuthService.AuthResult result = authService.register(
            TEST_EMAIL, TEST_PSEUDO, TEST_PASSWORD, TEST_PASSWORD);

        assertTrue(result.isSuccess(), "L'inscription devrait réussir");
        assertNotNull(result.getUser(), "L'utilisateur ne devrait pas être null");
        assertEquals(Role.ADMIN, result.getUser().getRole(), "Le premier utilisateur doit être admin");
    }

    @Test
    @Order(2)
    @DisplayName("Connexion avec des identifiants valides")
    void testLoginSuccess() {
        AuthService.AuthResult result = authService.login(TEST_EMAIL, TEST_PASSWORD);

        assertTrue(result.isSuccess(), "La connexion devrait réussir");
        assertNotNull(result.getUser(), "L'utilisateur ne devrait pas être null");
        assertEquals(TEST_EMAIL, result.getUser().getEmail());
    }

    @Test
    @Order(3)
    @DisplayName("Connexion avec mot de passe incorrect")
    void testLoginWrongPassword() {
        AuthService.AuthResult result = authService.login(TEST_EMAIL, "wrongpassword");

        assertFalse(result.isSuccess(), "La connexion ne devrait pas réussir");
        assertNull(result.getUser());
        assertEquals("Email ou mot de passe incorrect", result.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Connexion avec email inexistant")
    void testLoginUnknownEmail() {
        AuthService.AuthResult result = authService.login("unknown@test.com", TEST_PASSWORD);

        assertFalse(result.isSuccess(), "La connexion ne devrait pas réussir");
        assertEquals("Email ou mot de passe incorrect", result.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Inscription avec email invalide")
    void testRegisterInvalidEmail() {
        AuthService.AuthResult result = authService.register(
            "invalid-email", "pseudo", "password123", "password123");

        assertFalse(result.isSuccess());
        assertEquals("Format d'email invalide", result.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Inscription avec mot de passe trop court")
    void testRegisterShortPassword() {
        AuthService.AuthResult result = authService.register(
            "new@test.com", "pseudo", "123", "123");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("6 caractères"));
    }

    @Test
    @Order(7)
    @DisplayName("Inscription avec mots de passe non correspondants")
    void testRegisterPasswordMismatch() {
        AuthService.AuthResult result = authService.register(
            "new@test.com", "pseudo", "password123", "password456");

        assertFalse(result.isSuccess());
        assertEquals("Les mots de passe ne correspondent pas", result.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Inscription avec email déjà utilisé")
    void testRegisterDuplicateEmail() {
        AuthService.AuthResult result = authService.register(
            TEST_EMAIL, "NewPseudo", "password123", "password123");

        assertFalse(result.isSuccess());
        assertEquals("Un compte existe déjà avec cet email", result.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Inscription sans whitelist (non-premier utilisateur)")
    void testRegisterWithoutWhitelist() {
        AuthService.AuthResult result = authService.register(
            "nonwhitelisted@test.com", "pseudo", "password123", "password123");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("whitelisté") ||
                   result.getMessage().contains("autorisé"));
    }

    @Test
    @Order(10)
    @DisplayName("Déconnexion")
    void testLogout() {
        // D'abord se connecter
        authService.login(TEST_EMAIL, TEST_PASSWORD);
        assertTrue(authService.isLoggedIn());

        // Puis se déconnecter
        authService.logout();
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentUser());
    }
}
