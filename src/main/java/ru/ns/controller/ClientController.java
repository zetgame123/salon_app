package ru.ns.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.ns.dao.ClientDAO;
import ru.ns.model.Client;
import ru.ns.security.RoleService;

public class ClientController {

    @FXML
    private TableView<Client> clientTable;

    @FXML
    private TableColumn<Client, String> firstNameColumn;

    @FXML
    private TableColumn<Client, String> lastNameColumn;

    @FXML
    private TableColumn<Client, String> phoneColumn;

    @FXML
    private TableColumn<Client, Boolean> regularColumn;

    @FXML
    private Button deleteButton;

    private final ClientDAO clientDAO =
            new ClientDAO();

    @FXML
    public void initialize() {

        firstNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));

        lastNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("lastName"));

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone"));

        regularColumn.setCellValueFactory(
                new PropertyValueFactory<>("regular"));

        clientTable.setItems(
                FXCollections.observableArrayList(
                        clientDAO.findAll()
                )
        );

        deleteButton.setVisible(
                RoleService.canDeleteClients());
    }

    @FXML
    private void addClient() {

    }

    @FXML
    private void editClient() {

    }

    @FXML
    private void deleteClient() {

    }
}