package IStore.util;

import IStore.model.User;

public class SessionManager {

    private static User currentUser = null;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public static void logout() {
        currentUser = null;
    }

    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
}
