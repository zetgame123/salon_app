package ru.ns.security;

import ru.ns.model.User;

public class Session {

    private static User currentUser;

    public static void login(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isAuthorized() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null &&
                "ADMIN".equals(currentUser.getRoleName());
    }

    public static boolean isUser() {
        return currentUser != null &&
                "USER".equals(currentUser.getRoleName());
    }

    public static void logout() {
        currentUser = null;
    }
}