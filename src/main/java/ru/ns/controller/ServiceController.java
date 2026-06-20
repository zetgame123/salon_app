package ru.ns.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import ru.ns.dao.*;
import ru.ns.model.*;
import ru.ns.security.RoleService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class ServiceController {

    @FXML
    private TableView<ServiceHistory> historyTable;

    @FXML
    private TableColumn<ServiceHistory, String> surnameColumn;

    @FXML
    private TableColumn<ServiceHistory, String> nameColumn;

    @FXML
    private TableColumn<ServiceHistory, String> haircutColumn;

    @FXML
    private TableColumn<ServiceHistory, String> branchColumn;

    @FXML
    private TableColumn<ServiceHistory, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<ServiceHistory, BigDecimal> costColumn;

    @FXML
    private TableColumn<ServiceHistory, Boolean> discountColumn;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private final ServiceHistoryDAO historyDAO =
            new ServiceHistoryDAO();

    private final ServiceDAO serviceDAO =
            new ServiceDAO();

    private final ClientDAO clientDAO =
            new ClientDAO();

    private final BranchDAO branchDAO =
            new BranchDAO();

    private final PriceDAO priceDAO =
            new PriceDAO();

    @FXML
    public void initialize() {

        surnameColumn.setCellValueFactory(
                new PropertyValueFactory<>("surname"));

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        haircutColumn.setCellValueFactory(
                new PropertyValueFactory<>("haircut"));

        branchColumn.setCellValueFactory(
                new PropertyValueFactory<>("branch"));

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "serviceDate"));

        costColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "totalCost"));

        discountColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "discountApplied"));

        refreshTable();

        boolean admin = RoleService.canEditServices();

        editButton.setVisible(admin);
        deleteButton.setVisible(admin);
    }

    @FXML
    private void addService() {

        Service service = new Service();

        service.setServiceDate(LocalDateTime.now());

        if (!showServiceDialog(service, "Добавить услугу")) {
            return;
        }

        if (serviceDAO.save(service)) {
            refreshTable();
        } else {
            showError("Не удалось добавить услугу");
        }
    }

    @FXML
    private void editService() {

        ServiceHistory selected =
                historyTable.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            showError("Выберите услугу");
            return;
        }

        Service service =
                serviceDAO.findById(
                        selected.getIdService());

        if (service == null) {
            showError("Услуга не найдена");
            return;
        }

        if (!showServiceDialog(service, "Изменить услугу")) {
            return;
        }

        if (serviceDAO.update(service)) {
            refreshTable();
        } else {
            showError("Не удалось изменить услугу");
        }
    }

    @FXML
    private void deleteService() {

        ServiceHistory selected =
                historyTable.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            showError("Выберите услугу");
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Удаление");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Удалить услугу #" +
                        selected.getIdService() +
                        "?");

        Optional<ButtonType> result =
                confirm.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK
                && serviceDAO.delete(
                        selected.getIdService())) {
            refreshTable();
            return;
        }

        if (result.isPresent()
                && result.get() == ButtonType.OK) {
            showError("Не удалось удалить услугу");
        }
    }

    private void refreshTable() {

        ObservableList<ServiceHistory> items =
                FXCollections.observableArrayList(
                        historyDAO.findAll());

        historyTable.setItems(items);
    }

    private boolean showServiceDialog(
            Service service,
            String title) {

        List<Client> clients = clientDAO.findAll();
        List<Branch> branches = branchDAO.findAll();
        List<PriceListItem> prices =
                priceDAO.findAllWithHaircut();

        if (clients.isEmpty()
                || branches.isEmpty()
                || prices.isEmpty()) {
            showError(
                    "Заполните справочники: " +
                            "клиенты, филиалы и прайс");
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

        ComboBox<Client> clientCombo =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                clients));

        ComboBox<Branch> branchCombo =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                branches));

        ComboBox<PriceListItem> priceCombo =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                prices));

        DatePicker datePicker = new DatePicker();

        TextArea wishesArea = new TextArea();

        if (service.getClientWishes() != null) {
            wishesArea.setText(service.getClientWishes());
        }

        wishesArea.setPromptText(
                "Необязательно");

        wishesArea.setPrefRowCount(3);

        clientCombo.setConverter(clientConverter());
        branchCombo.setConverter(branchConverter());

        clientCombo.setMaxWidth(Double.MAX_VALUE);
        branchCombo.setMaxWidth(Double.MAX_VALUE);
        priceCombo.setMaxWidth(Double.MAX_VALUE);

        selectClient(clientCombo, clients, service);
        selectBranch(branchCombo, branches, service);
        selectPrice(priceCombo, prices, service);

        if (service.getIdService() == 0) {
            setDefaultIfEmpty(clientCombo, clients);
            setDefaultIfEmpty(branchCombo, branches);
            setDefaultIfEmpty(priceCombo, prices);
        }

        if (service.getServiceDate() != null) {
            datePicker.setValue(
                    service.getServiceDate()
                            .toLocalDate());
        } else {
            datePicker.setValue(LocalDate.now());
        }

        grid.add(new Label("Клиент:"), 0, 0);
        grid.add(clientCombo, 1, 0);
        grid.add(new Label("Филиал:"), 0, 1);
        grid.add(branchCombo, 1, 1);
        grid.add(new Label("Прайс:"), 0, 2);
        grid.add(priceCombo, 1, 2);
        grid.add(new Label("Дата:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Пожелания:"), 0, 4);
        grid.add(wishesArea, 1, 4);

        dialog.getDialogPane().setPrefWidth(450);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result =
                dialog.showAndWait();

        if (result.isEmpty()
                || result.get() != ButtonType.OK) {
            return false;
        }

        if (clientCombo.getValue() == null
                || branchCombo.getValue() == null
                || priceCombo.getValue() == null
                || datePicker.getValue() == null) {
            showError("Заполните все обязательные поля");
            return false;
        }

        LocalTime time = service.getServiceDate() != null
                ? service.getServiceDate().toLocalTime()
                : LocalTime.now();

        service.setIdClient(
                clientCombo.getValue().getIdClient());

        service.setIdBranch(
                branchCombo.getValue().getIdBranch());

        service.setIdPrice(
                priceCombo.getValue().getIdPrice());

        service.setServiceDate(
                datePicker.getValue().atTime(time));

        String wishes = wishesArea.getText().trim();

        service.setClientWishes(
                wishes.isEmpty() ? null : wishes);

        return true;
    }

    private <T> void setDefaultIfEmpty(
            ComboBox<T> combo,
            List<T> items) {

        if (combo.getValue() == null && !items.isEmpty()) {
            combo.setValue(items.getFirst());
        }
    }

    private StringConverter<Client> clientConverter() {

        return new StringConverter<>() {

            @Override
            public String toString(Client client) {

                if (client == null) {
                    return "";
                }

                return client.getSurname() +
                        " " +
                        client.getName() +
                        " (" +
                        client.getPhone() +
                        ")";
            }

            @Override
            public Client fromString(String string) {
                return null;
            }
        };
    }

    private StringConverter<Branch> branchConverter() {

        return new StringConverter<>() {

            @Override
            public String toString(Branch branch) {

                if (branch == null) {
                    return "";
                }

                return branch.getName();
            }

            @Override
            public Branch fromString(String string) {
                return null;
            }
        };
    }

    private void selectClient(
            ComboBox<Client> combo,
            List<Client> clients,
            Service service) {

        clients.stream()
                .filter(c -> c.getIdClient()
                        == service.getIdClient())
                .findFirst()
                .ifPresent(combo::setValue);
    }

    private void selectBranch(
            ComboBox<Branch> combo,
            List<Branch> branches,
            Service service) {

        branches.stream()
                .filter(b -> b.getIdBranch()
                        == service.getIdBranch())
                .findFirst()
                .ifPresent(combo::setValue);
    }

    private void selectPrice(
            ComboBox<PriceListItem> combo,
            List<PriceListItem> prices,
            Service service) {

        prices.stream()
                .filter(p -> p.getIdPrice()
                        == service.getIdPrice())
                .findFirst()
                .ifPresent(combo::setValue);
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
