package ru.ns.security;

import ru.ns.dao.UserDAO;
import ru.ns.model.User;

import java.time.LocalDate;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        userDAO = new UserDAO();
    }

    public boolean login(String login, String password) {

        User user = userDAO.findByLogin(login);

        if (user == null) {
            return false;
        }

        boolean valid =
                PasswordUtils.verifyPassword(
                        password,
                        user.getPasswordHash()
                );

        if (valid) {
            Session.login(user);
            return true;
        }

        return false;
    }

    public boolean register(
            String login,
            String phone,
            String email,
            String password) {

        if (userDAO.loginExists(login)) {
            return false;
        }

        if (userDAO.emailExists(email)) {
            return false;
        }

        if (!PasswordValidator.isValid(password)) {
            return false;
        }

        User user = new User();

        user.setIdRole(2); // USER

        user.setLogin(login);

        user.setPhone(phone);

        user.setEmail(email);

        user.setRegistrationDate(
                LocalDate.now());

        user.setPasswordHash(
                PasswordUtils.hashPassword(
                        password));

        return userDAO.save(user);
    }

    public void logout() {
        Session.logout();
    }

}