package IStore.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_PSEUDO_LENGTH = 2;

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "L'email est requis";
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Format d'email invalide";
        }
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Le mot de passe est requis";
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères";
        }
        return null;
    }

    public static String validatePseudo(String pseudo) {
        if (pseudo == null || pseudo.trim().isEmpty()) {
            return "Le pseudo est requis";
        }
        if (pseudo.trim().length() < MIN_PSEUDO_LENGTH) {
            return "Le pseudo doit contenir au moins " + MIN_PSEUDO_LENGTH + " caractères";
        }
        return null;
    }

    public static String validateStoreName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Le nom du magasin est requis";
        }
        if (name.trim().length() < 2) {
            return "Le nom du magasin doit contenir au moins 2 caractères";
        }
        return null;
    }

    public static String validateItemName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Le nom de l'article est requis";
        }
        return null;
    }

    public static String validatePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return "Le prix est requis";
        }
        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price < 0) {
                return "Le prix ne peut pas être négatif";
            }
        } catch (NumberFormatException e) {
            return "Le prix doit être un nombre valide";
        }
        return null;
    }

    public static String validateQuantity(String quantityStr) {
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            return "La quantité est requise";
        }
        try {
            int quantity = Integer.parseInt(quantityStr.trim());
            if (quantity < 0) {
                return "La quantité ne peut pas être négative";
            }
        } catch (NumberFormatException e) {
            return "La quantité doit être un nombre entier";
        }
        return null;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
