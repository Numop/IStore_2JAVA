package IStore.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int BCRYPT_ROUNDS = 12;

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
