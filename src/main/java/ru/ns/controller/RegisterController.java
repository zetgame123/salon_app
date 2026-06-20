package ru.ns.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.ns.security.AuthService;

public class RegisterController {

    @FXML
    private TextField loginField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private final AuthService authService =
            new AuthService();

    @FXML
    private void register() {

        String error = authService.register(
                loginField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText(),
                confirmPasswordField.getText());

        if (error == null) {
            goBack();
            return;
        }

        messageLabel.setText(error);
    }

    @FXML
    private void goBack() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ru/ns/login.fxml"));

            Stage loginStage = new Stage();

            loginStage.setScene(
                    new Scene(loader.load()));

            loginStage.setTitle("Авторизация");

            loginStage.show();

            Stage current =
                    (Stage) loginField
                            .getScene()
                            .getWindow();

            current.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
