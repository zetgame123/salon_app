package ru.ns.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.ns.security.AuthService;
import ru.ns.security.Session;

public class ProfileController {

    @FXML
    private Label loginLabel;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    private final AuthService authService =
            new AuthService();

    @FXML
    public void initialize() {

        loginLabel.setText(
                "Логин: " +
                        Session.getCurrentUser().getLogin());

        phoneField.setText(
                Session.getCurrentUser().getPhone());

        emailField.setText(
                Session.getCurrentUser().getEmail());
    }

    @FXML
    private void save() {

        String error = authService.updateProfile(
                phoneField.getText().trim(),
                emailField.getText().trim());

        if (error == null) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Данные сохранены");
            return;
        }

        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(error);
    }

    @FXML
    private void close() {

        Stage stage =
                (Stage) phoneField
                        .getScene()
                        .getWindow();

        stage.close();
    }
}
