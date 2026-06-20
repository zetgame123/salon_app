package ru.ns.security;

import ru.ns.dao.UserDAO;
import ru.ns.model.User;

import java.time.LocalDateTime;

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

    public String register(
            String login,
            String phone,
            String email,
            String password,
            String confirmPassword) {

        if (login.isBlank() || phone.isBlank()
                || email.isBlank()
                || password.isBlank()
                || confirmPassword.isBlank()) {
            return "Заполните все поля";
        }

        if (!password.equals(confirmPassword)) {
            return "Пароли не совпадают";
        }

        if (!PasswordValidator.isValid(password)) {
            return "Пароль: мин. 8 символов, цифра, заглавная буква, спецсимвол";
        }

        if (userDAO.loginExists(login)) {
            return "Логин уже занят";
        }

        if (userDAO.emailExists(email)) {
            return "Email уже используется";
        }

        User user = new User();

        user.setIdRole(2); // USER
        user.setLogin(login);
        user.setPhone(phone);
        user.setEmail(email);
        user.setRegistrationDate(LocalDateTime.now());
        user.setPasswordHash(
                PasswordUtils.hashPassword(password));

        if (!userDAO.save(user)) {
            return "Не удалось зарегистрироваться";
        }

        return null;
    }

    public String updateProfile(
            String phone,
            String email) {

        if (!Session.isAuthorized()) {
            return "Пользователь не авторизован";
        }

        if (phone.isBlank() || email.isBlank()) {
            return "Заполните все поля";
        }

        User currentUser = Session.getCurrentUser();

        if (userDAO.emailExistsForOtherUser(
                email,
                currentUser.getIdUser())) {
            return "Email уже используется";
        }

        currentUser.setPhone(phone);
        currentUser.setEmail(email);

        if (!userDAO.update(currentUser)) {
            return "Не удалось сохранить данные";
        }

        User updatedUser =
                userDAO.findById(
                        currentUser.getIdUser());

        if (updatedUser != null) {
            Session.login(updatedUser);
        }

        return null;
    }

    public void logout() {
        Session.logout();
    }

}
