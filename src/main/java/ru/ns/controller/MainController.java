package ru.ns.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ru.ns.security.AuthService;
import ru.ns.security.RoleService;
import ru.ns.security.Session;

public class MainController {

    @FXML
    private Label userLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Button usersButton;

    @FXML
    private Button pricesButton;

    private final AuthService authService =
            new AuthService();

    @FXML
    public void initialize() {

        userLabel.setText(
                "Пользователь: " +
                        Session.getCurrentUser().getLogin());

        roleLabel.setText(
                "Роль: " +
                        Session.getCurrentUser().getRoleName());

        usersButton.setVisible(
                RoleService.canManageUsers());

        pricesButton.setVisible(
                RoleService.canEditPrices());
    }

    @FXML
    private void logout() {

        authService.logout();

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
                    (Stage) userLabel
                            .getScene()
                            .getWindow();

            current.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openClients() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ru/ns/clients.fxml"));

            Stage stage = new Stage();

            stage.setScene(
                    new Scene(loader.load()));

            stage.setTitle("Клиенты");

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openServices() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ru/ns/services.fxml"));

            Stage stage = new Stage();

            stage.setScene(
                    new Scene(loader.load()));

            stage.setTitle("Услуги");

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openProfile() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ru/ns/profile.fxml"));

            Stage stage = new Stage();

            stage.setScene(
                    new Scene(loader.load()));

            stage.setTitle("Мой профиль");

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}