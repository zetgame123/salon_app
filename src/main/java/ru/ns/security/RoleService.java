package ru.ns.security;

public class RoleService {

    public static boolean canViewClients() {
        return Session.isAuthorized();
    }

    public static boolean canCreateService() {
        return Session.isAuthorized();
    }

    public static boolean canDeleteClients() {
        return Session.isAdmin();
    }

    public static boolean canEditPrices() {
        return Session.isAdmin();
    }

    public static boolean canManageUsers() {
        return Session.isAdmin();
    }
}