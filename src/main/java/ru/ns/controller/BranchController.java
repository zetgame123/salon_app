package ru.ns.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import ru.ns.dao.BranchDAO;
import ru.ns.model.Branch;

import java.util.Optional;

public class BranchController {

    @FXML
    private TableView<Branch> branchTable;

    @FXML
    private TableColumn<Branch, String> nameColumn;

    @FXML
    private TableColumn<Branch, String> addressColumn;

    private final BranchDAO branchDAO = new BranchDAO();

    @FXML
    public void initialize() {

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        addressColumn.setCellValueFactory(
                new PropertyValueFactory<>("address"));

        refreshTable();
    }

    @FXML
    private void addBranch() {

        Branch branch = new Branch();

        if (!showBranchDialog(branch, "Добавить филиал")) {
            return;
        }

        if (branchDAO.save(branch)) {
            refreshTable();
        } else {
            showError("Не удалось добавить филиал");
        }
    }

    @FXML
    private void editBranch() {

        Branch selected =
                branchTable.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            showError("Выберите филиал");
            return;
        }

        Branch branch = copyBranch(selected);

        if (!showBranchDialog(branch, "Изменить филиал")) {
            return;
        }

        if (branchDAO.update(branch)) {
            refreshTable();
        } else {
            showError("Не удалось изменить филиал");
        }
    }

    @FXML
    private void deleteBranch() {

        Branch selected =
                branchTable.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            showError("Выберите филиал");
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Удаление");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Удалить филиал «" +
                        selected.getName() +
                        "»?");

        Optional<ButtonType> result =
                confirm.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK
                && branchDAO.delete(
                        selected.getIdBranch())) {
            refreshTable();
            return;
        }

        if (result.isPresent()
                && result.get() == ButtonType.OK) {
            showError(
                    "Не удалось удалить. " +
                            "Филиал используется в услугах");
        }
    }

    private void refreshTable() {

        ObservableList<Branch> branches =
                FXCollections.observableArrayList(
                        branchDAO.findAll());

        branchTable.setItems(branches);
    }

    private Branch copyBranch(Branch source) {

        Branch branch = new Branch();

        branch.setIdBranch(source.getIdBranch());
        branch.setName(source.getName());
        branch.setAddress(source.getAddress());

        return branch;
    }

    private boolean showBranchDialog(
            Branch branch,
            String title) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField =
                new TextField(branch.getName());

        TextField addressField =
                new TextField(branch.getAddress());

        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Адрес:"), 0, 1);
        grid.add(addressField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(400);

        Optional<ButtonType> result =
                dialog.showAndWait();

        if (result.isEmpty()
                || result.get() != ButtonType.OK) {
            return false;
        }

        if (nameField.getText().isBlank()
                || addressField.getText().isBlank()) {
            showError("Заполните все поля");
            return false;
        }

        branch.setName(nameField.getText().trim());
        branch.setAddress(addressField.getText().trim());

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
