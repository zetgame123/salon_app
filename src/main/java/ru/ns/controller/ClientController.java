package ru.ns.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ru.ns.dao.ClientDAO;
import ru.ns.model.Client;
import ru.ns.security.RoleService;

import java.util.Optional;

public class ClientController {

    @FXML
    private TableView<Client> clientTable;

    @FXML
    private TableColumn<Client, String> surnameColumn;

    @FXML
    private TableColumn<Client, String> nameColumn;

    @FXML
    private TableColumn<Client, String> patronymicColumn;

    @FXML
    private TableColumn<Client, String> phoneColumn;

    @FXML
    private TableColumn<Client, Boolean> regularClientColumn;

    @FXML
    private Button deleteButton;

    private final ClientDAO clientDAO =
            new ClientDAO();

    private ObservableList<Client> clients;

    @FXML
    public void initialize() {

        surnameColumn.setCellValueFactory(
                new PropertyValueFactory<>("surname"));

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        patronymicColumn.setCellValueFactory(
                new PropertyValueFactory<>("patronymic"));

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone"));

        regularClientColumn.setCellValueFactory(
                new PropertyValueFactory<>("regularClient"));

        refreshTable();

        deleteButton.setVisible(
                RoleService.canDeleteClients());
    }

    @FXML
    private void addClient() {

        Client client = new Client();

        if (!showClientDialog(client, "Добавить клиента")) {
            return;
        }

        if (clientDAO.save(client)) {
            refreshTable();
        } else {
            showError(
                    "Не удалось добавить клиента. " +
                            "Возможно, телефон уже используется");
        }
    }

    @FXML
    private void editClient() {

        Client selected =
                clientTable.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            showError("Выберите клиента");
            return;
        }

        Client client = copyClient(selected);

        if (!showClientDialog(client, "Изменить клиента")) {
            return;
        }

        if (clientDAO.update(client)) {
            refreshTable();
        } else {
            showError(
                    "Не удалось изменить клиента. " +
                            "Возможно, телефон уже используется");
        }
    }

    @FXML
    private void deleteClient() {

        Client selected =
                clientTable.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            showError("Выберите клиента");
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Удаление");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Удалить клиента " +
                        selected.getSurname() +
                        " " +
                        selected.getName() +
                        "?");

        Optional<ButtonType> result =
                confirm.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK
                && clientDAO.delete(
                        selected.getIdClient())) {
            refreshTable();
            return;
        }

        if (result.isPresent()
                && result.get() == ButtonType.OK) {
            showError("Не удалось удалить клиента");
        }
    }

    private void refreshTable() {

        clients = FXCollections.observableArrayList(
                clientDAO.findAll());

        clientTable.setItems(clients);
    }

    private Client copyClient(Client source) {

        Client client = new Client();

        client.setIdClient(source.getIdClient());
        client.setSurname(source.getSurname());
        client.setName(source.getName());
        client.setPatronymic(source.getPatronymic());
        client.setPhone(source.getPhone());
        client.setRegularClient(
                source.isRegularClient());

        return client;
    }

    private boolean showClientDialog(
            Client client,
            String title) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField surnameField =
                new TextField(client.getSurname());

        TextField nameField =
                new TextField(client.getName());

        TextField patronymicField =
                new TextField(client.getPatronymic());

        TextField phoneField =
                new TextField(client.getPhone());

        grid.add(new Label("Фамилия:"), 0, 0);
        grid.add(surnameField, 1, 0);
        grid.add(new Label("Имя:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2);
        grid.add(patronymicField, 1, 2);
        grid.add(new Label("Телефон:"), 0, 3);
        grid.add(phoneField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result =
                dialog.showAndWait();

        if (result.isEmpty()
                || result.get() != ButtonType.OK) {
            return false;
        }

        if (surnameField.getText().isBlank()
                || nameField.getText().isBlank()
                || phoneField.getText().isBlank()) {
            showError("Заполните фамилию, имя и телефон");
            return false;
        }

        client.setSurname(
                surnameField.getText().trim());

        client.setName(
                nameField.getText().trim());

        client.setPatronymic(
                patronymicField.getText().trim());

        client.setPhone(
                phoneField.getText().trim());

        return true;
    }

    private void showError(String message) {

        Alert alert = new Alert(
                Alert.AlertType.ERROR);

        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
