package ru.ns.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.ns.security.AuthService;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final AuthService authService =
            new AuthService();

    @FXML
    private void login() {

        String login =
                loginField.getText();

        String password =
                passwordField.getText();

        boolean success =
                authService.login(
                        login,
                        password);

        if (success) {
            messageLabel.setText(
                    "Вход выполнен");
        } else {
            messageLabel.setText(
                    "Неверный логин или пароль");
        }
    }
}