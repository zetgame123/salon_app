package ru.ns.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import ru.ns.dao.RoleDAO;
import ru.ns.dao.UserDAO;
import ru.ns.model.Role;
import ru.ns.model.User;
import ru.ns.security.AuthService;
import ru.ns.security.Session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> loginColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> phoneColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, LocalDateTime> dateColumn;

    private final UserDAO userDAO = new UserDAO();

    private final RoleDAO roleDAO = new RoleDAO();

    private final AuthService authService =
            new AuthService();

    @FXML
    public void initialize() {

        loginColumn.setCellValueFactory(
                new PropertyValueFactory<>("login"));

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone"));

        roleColumn.setCellValueFactory(
                new PropertyValueFactory<>("roleName"));

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "registrationDate"));

        refreshTable();
    }

    @FXML
    private void addUser() {

        if (!showUserDialog(null, "Добавить пользователя")) {
            return;
        }

        refreshTable();
    }

    @FXML
    private void editUser() {

        User selected =
                userTable.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            showError("Выберите пользователя");
            return;
        }

        User user = copyUser(selected);

        if (!showUserDialog(user, "Изменить пользователя")) {
            return;
        }

        if (userDAO.updateByAdmin(user)) {

            if (user.getIdUser()
                    == Session.getCurrentUser()
                    .getIdUser()) {

                User updated =
                        userDAO.findById(
                                user.getIdUser());

                if (updated != null) {
                    Session.login(updated);
                }
            }

            refreshTable();
        } else {
            showError("Не удалось сохранить данные");
        }
    }

    @FXML
    private void deleteUser() {

        User selected =
                userTable.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            showError("Выберите пользователя");
            return;
        }

        if (selected.getIdUser()
                == Session.getCurrentUser().getIdUser()) {
            showError("Нельзя удалить себя");
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Удаление");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Удалить пользователя «" +
                        selected.getLogin() +
                        "»?");

        Optional<ButtonType> result =
                confirm.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK
                && userDAO.delete(
                        selected.getIdUser())) {
            refreshTable();
            return;
        }

        if (result.isPresent()
                && result.get() == ButtonType.OK) {
            showError("Не удалось удалить пользователя");
        }
    }

    private void refreshTable() {

        ObservableList<User> users =
                FXCollections.observableArrayList(
                        userDAO.findAll());

        userTable.setItems(users);
    }

    private User copyUser(User source) {

        User user = new User();

        user.setIdUser(source.getIdUser());
        user.setIdRole(source.getIdRole());
        user.setRoleName(source.getRoleName());
        user.setLogin(source.getLogin());
        user.setPhone(source.getPhone());
        user.setEmail(source.getEmail());
        user.setRegistrationDate(
                source.getRegistrationDate());

        return user;
    }

    private boolean showUserDialog(
            User user,
            String title) {

        boolean isNew = user == null;

        User dialogUser = isNew ? new User() : user;

        List<Role> roles = roleDAO.findAll();

        if (roles.isEmpty()) {
            showError("Справочник ролей пуст");
            return false;
        }

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField loginField = new TextField(dialogUser.getLogin());

        TextField phoneField = new TextField(dialogUser.getPhone());

        TextField emailField = new TextField(dialogUser.getEmail());

        PasswordField passwordField =
                new PasswordField();

        PasswordField confirmField =
                new PasswordField();

        ComboBox<Role> roleCombo =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                roles));

        roleCombo.setConverter(roleConverter());
        roleCombo.setMaxWidth(Double.MAX_VALUE);

        roles.stream()
                .filter(r -> r.getIdRole()
                        == dialogUser.getIdRole())
                .findFirst()
                .ifPresent(roleCombo::setValue);

        if (roleCombo.getValue() == null) {
            roleCombo.setValue(roles.getLast());
        }

        int row = 0;

        grid.add(new Label("Логин:"), 0, row);
        grid.add(loginField, 1, row++);

        grid.add(new Label("Телефон:"), 0, row);
        grid.add(phoneField, 1, row++);

        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);

        grid.add(new Label("Роль:"), 0, row);
        grid.add(roleCombo, 1, row++);

        if (isNew) {
            grid.add(new Label("Пароль:"), 0, row);
            grid.add(passwordField, 1, row++);
            grid.add(new Label("Подтверждение:"), 0, row);
            grid.add(confirmField, 1, row++);
        } else {
            loginField.setEditable(false);
        }

        boolean isSelf = !isNew
                && dialogUser.getIdUser()
                == Session.getCurrentUser().getIdUser();

        if (isSelf) {
            roleCombo.setDisable(true);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(400);

        Optional<ButtonType> result =
                dialog.showAndWait();

        if (result.isEmpty()
                || result.get() != ButtonType.OK) {
            return false;
        }

        if (loginField.getText().isBlank()
                || phoneField.getText().isBlank()
                || emailField.getText().isBlank()
                || roleCombo.getValue() == null) {
            showError("Заполните все поля");
            return false;
        }

        if (isNew) {

            String error = authService.register(
                    loginField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim(),
                    passwordField.getText(),
                    confirmField.getText());

            if (error != null) {
                showError(error);
                return false;
            }

            User created =
                    userDAO.findByLogin(
                            loginField.getText().trim());

            if (created == null) {
                showError("Пользователь не найден");
                return false;
            }

            if (roleCombo.getValue().getIdRole() != 2) {
                created.setIdRole(
                        roleCombo.getValue().getIdRole());

                if (!userDAO.updateByAdmin(created)) {
                    showError(
                            "Не удалось назначить роль");
                    return false;
                }
            }

            return true;
        }

        if (userDAO.emailExistsForOtherUser(
                emailField.getText().trim(),
                dialogUser.getIdUser())) {
            showError("Email уже используется");
            return false;
        }

        dialogUser.setPhone(
                phoneField.getText().trim());

        dialogUser.setEmail(
                emailField.getText().trim());

        if (!isSelf) {
            dialogUser.setIdRole(
                    roleCombo.getValue().getIdRole());
            dialogUser.setRoleName(
                    roleCombo.getValue().getRoleName());
        }

        return true;
    }

    private StringConverter<Role> roleConverter() {

        return new StringConverter<>() {

            @Override
            public String toString(Role role) {

                if (role == null) {
                    return "";
                }

                return role.getRoleName();
            }

            @Override
            public Role fromString(String string) {
                return null;
            }
        };
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
