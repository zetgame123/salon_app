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
    private Button branchesButton;

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

        branchesButton.setVisible(
                RoleService.canManageBranches());

        pricesButton.setVisible(
                RoleService.canViewPrices());
    }

    @FXML
    private void logout() {

        authService.logout();
        openWindow("/ru/ns/login.fxml", "Авторизация", true);
    }

    @FXML
    private void openClients() {
        openWindow("/ru/ns/clients.fxml", "Клиенты");
    }

    @FXML
    private void openServices() {
        openWindow("/ru/ns/services.fxml", "Услуги");
    }

    @FXML
    private void openPrices() {
        openWindow("/ru/ns/prices.fxml", "Прайс");
    }

    @FXML
    private void openUsers() {
        openWindow("/ru/ns/users.fxml", "Пользователи");
    }

    @FXML
    private void openBranches() {
        openWindow("/ru/ns/branches.fxml", "Филиалы");
    }

    @FXML
    private void openProfile() {
        openWindow("/ru/ns/profile.fxml", "Мой профиль");
    }

    private void openWindow(String fxmlResource, String title) {
        openWindow(fxmlResource, title, false);
    }

    private void openWindow(
            String fxmlResource,
            String title,
            boolean closeCurrent) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    fxmlResource));

            Stage stage = new Stage();

            stage.setScene(
                    new Scene(loader.load()));

            stage.setTitle(title);
            stage.show();

            if (closeCurrent) {
                Stage current =
                        (Stage) userLabel
                                .getScene()
                                .getWindow();

                current.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
