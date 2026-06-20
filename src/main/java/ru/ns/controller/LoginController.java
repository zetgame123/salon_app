package ru.ns.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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

        boolean success =
                authService.login(
                        loginField.getText(),
                        passwordField.getText());

        if (success) {

            try {

                FXMLLoader loader =
                        new FXMLLoader(
                                getClass().getResource(
                                        "/ru/ns/main.fxml"));

                Scene scene =
                        new Scene(loader.load());

                Stage stage =
                        new Stage();

                stage.setScene(scene);

                stage.setTitle("Главное меню");

                stage.show();

                Stage current =
                        (Stage) loginField
                                .getScene()
                                .getWindow();

                current.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            messageLabel.setText(
                    "Неверный логин или пароль");
        }
    }

}
