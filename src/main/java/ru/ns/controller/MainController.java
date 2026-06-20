package ru.ns.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
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
        Session.logout();
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
}