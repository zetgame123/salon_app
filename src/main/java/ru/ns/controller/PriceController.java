package ru.ns.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import ru.ns.dao.HaircutTypeDAO;
import ru.ns.dao.PriceDAO;
import ru.ns.model.HaircutType;
import ru.ns.model.Price;
import ru.ns.model.PriceListItem;
import ru.ns.security.RoleService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PriceController {

    @FXML
    private TableView<PriceListItem> priceTable;

    @FXML
    private TableColumn<PriceListItem, String> haircutColumn;

    @FXML
    private TableColumn<PriceListItem, String> genderColumn;

    @FXML
    private TableColumn<PriceListItem, LocalDate> startDateColumn;

    @FXML
    private TableColumn<PriceListItem, BigDecimal> priceColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addTypeButton;

    private final PriceDAO priceDAO = new PriceDAO();

    private final HaircutTypeDAO haircutTypeDAO =
            new HaircutTypeDAO();

    @FXML
    public void initialize() {

        haircutColumn.setCellValueFactory(
                new PropertyValueFactory<>("haircutName"));

        genderColumn.setCellValueFactory(
                new PropertyValueFactory<>("gender"));

        startDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("startDate"));

        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        refreshTable();

        boolean canEdit = RoleService.canEditPrices();

        addButton.setVisible(canEdit);
        editButton.setVisible(canEdit);
        deleteButton.setVisible(canEdit);
        addTypeButton.setVisible(canEdit);
    }

    @FXML
    private void addPrice() {

        List<HaircutType> types =
                haircutTypeDAO.findAll();

        if (types.isEmpty()) {
            showError(
                    "Сначала добавьте тип стрижки");
            return;
        }

        Price price = new Price();

        price.setStartDate(LocalDate.now());

        if (!showPriceDialog(price, types, "Добавить цену")) {
            return;
        }

        if (priceDAO.save(price)) {
            refreshTable();
        } else {
            showError(
                    "Не удалось добавить цену. " +
                            "Возможно, для этой стрижки " +
                            "уже есть цена с такой датой");
        }
    }

    @FXML
    private void editPrice() {

        PriceListItem selected =
                priceTable.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            showError("Выберите запись");
            return;
        }

        List<HaircutType> types =
                haircutTypeDAO.findAll();

        Price price =
                priceDAO.findById(
                        selected.getIdPrice());

        if (price == null) {
            showError("Запись не найдена");
            return;
        }

        if (!showPriceDialog(price, types, "Изменить цену")) {
            return;
        }

        if (priceDAO.update(price)) {
            refreshTable();
        } else {
            showError(
                    "Не удалось изменить цену. " +
                            "Возможно, дата уже занята");
        }
    }

    @FXML
    private void deletePrice() {

        PriceListItem selected =
                priceTable.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            showError("Выберите запись");
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Удаление");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Удалить цену для «" +
                        selected.getHaircutName() +
                        "»?");

        Optional<ButtonType> result =
                confirm.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK
                && priceDAO.delete(
                        selected.getIdPrice())) {
            refreshTable();
            return;
        }

        if (result.isPresent()
                && result.get() == ButtonType.OK) {
            showError(
                    "Не удалось удалить. " +
                            "Цена используется в услугах");
        }
    }

    @FXML
    private void addHaircutType() {

        if (!showHaircutTypeDialog()) {
            return;
        }

        refreshTable();
    }

    private void refreshTable() {

        ObservableList<PriceListItem> items =
                FXCollections.observableArrayList(
                        priceDAO.findAllWithHaircut());

        priceTable.setItems(items);
    }

    private boolean showPriceDialog(
            Price price,
            List<HaircutType> types,
            String title) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<HaircutType> typeCombo =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                types));

        DatePicker datePicker = new DatePicker();

        TextField priceField = new TextField();

        typeCombo.setConverter(haircutConverter());
        typeCombo.setMaxWidth(Double.MAX_VALUE);

        types.stream()
                .filter(t -> t.getIdHaircutType()
                        == price.getIdHaircutType())
                .findFirst()
                .ifPresent(typeCombo::setValue);

        if (typeCombo.getValue() == null
                && !types.isEmpty()) {
            typeCombo.setValue(types.getFirst());
        }

        if (price.getStartDate() != null) {
            datePicker.setValue(price.getStartDate());
        } else {
            datePicker.setValue(LocalDate.now());
        }

        if (price.getPrice() != null) {
            priceField.setText(
                    price.getPrice().toPlainString());
        }

        grid.add(new Label("Стрижка:"), 0, 0);
        grid.add(typeCombo, 1, 0);
        grid.add(new Label("Дата начала:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Цена:"), 0, 2);
        grid.add(priceField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(400);

        Optional<ButtonType> result =
                dialog.showAndWait();

        if (result.isEmpty()
                || result.get() != ButtonType.OK) {
            return false;
        }

        if (typeCombo.getValue() == null
                || datePicker.getValue() == null
                || priceField.getText().isBlank()) {
            showError("Заполните все поля");
            return false;
        }

        try {
            BigDecimal amount =
                    new BigDecimal(
                            priceField.getText()
                                    .trim()
                                    .replace(',', '.'));

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Цена должна быть больше 0");
                return false;
            }

            price.setPrice(amount);

        } catch (NumberFormatException e) {
            showError("Некорректная цена");
            return false;
        }

        price.setIdHaircutType(
                typeCombo.getValue().getIdHaircutType());

        price.setStartDate(datePicker.getValue());

        return true;
    }

    private boolean showHaircutTypeDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Добавить тип стрижки");
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();

        ComboBox<String> genderCombo =
                new ComboBox<>(FXCollections
                        .observableArrayList(
                                "Мужской",
                                "Женский",
                                "Унисекс"));

        genderCombo.getSelectionModel().selectFirst();

        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Пол:"), 0, 1);
        grid.add(genderCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result =
                dialog.showAndWait();

        if (result.isEmpty()
                || result.get() != ButtonType.OK) {
            return false;
        }

        if (nameField.getText().isBlank()
                || genderCombo.getValue() == null) {
            showError("Заполните все поля");
            return false;
        }

        HaircutType type = new HaircutType();

        type.setName(nameField.getText().trim());
        type.setGender(genderCombo.getValue());

        if (haircutTypeDAO.save(type)) {
            return true;
        }

        showError("Не удалось добавить тип стрижки");
        return false;
    }

    private StringConverter<HaircutType> haircutConverter() {

        return new StringConverter<>() {

            @Override
            public String toString(HaircutType type) {

                if (type == null) {
                    return "";
                }

                return type.getName() +
                        " (" +
                        type.getGender() +
                        ")";
            }

            @Override
            public HaircutType fromString(String string) {
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
