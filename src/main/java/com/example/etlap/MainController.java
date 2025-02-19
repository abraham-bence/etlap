package com.example.etlap;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class MainController {

    @javafx.fxml.FXML
    private TableColumn<Menu, Integer> price;
    @javafx.fxml.FXML
    private TableColumn<Menu, String> name;
    @javafx.fxml.FXML
    private TableView<Menu> menu;
    @javafx.fxml.FXML
    private TableColumn<Menu, String> category;
    @javafx.fxml.FXML
    private Text description;

    private Menu menuItem;

    private MenuService menuService;
    @javafx.fxml.FXML
    private Button addBtn;
    @javafx.fxml.FXML
    private Button deleteBtn;
    @javafx.fxml.FXML
    private ChoiceBox increaseTypeInput;
    @javafx.fxml.FXML
    private Spinner increaseAmountInput;
    @javafx.fxml.FXML
    private Button increaceBtn;

    public void initialize() {
        name.setCellValueFactory(new PropertyValueFactory<Menu, String>("name"));
        price.setCellValueFactory(new PropertyValueFactory<Menu, Integer>("price"));
        category.setCellValueFactory(new PropertyValueFactory<Menu, String>("category"));
        increaseAmountInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(50,3000,50,50));
        increaseTypeInput.setItems(FXCollections.observableArrayList("Ft","%"));
        increaseTypeInput.getSelectionModel().selectFirst();

        try {
            menuService = new MenuService();
            listMenu();
        }
        catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba!");
            alert.setHeaderText("Nem sikerült kapcsolódni az adatbázishoz");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            Platform.exit();
        }
        selectedItem();

    }

    private void selectedItem() {
        menu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                menuItem = newValue;
                description.setText(newValue.getDescription());
            }
        });
    }

    private void listMenu() throws SQLException {
        menu.getItems().clear();
        menu.getItems().addAll(menuService.getAll());
    }


    @javafx.fxml.FXML
    public void addBtnOnClick(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("addMenuItem.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 480);
            AddMenuItem controller = fxmlLoader.getController();
            controller.setMenuService(menuService);
            stage.setTitle("Új étel hozzáadása");
            stage.setScene(scene);
            stage.setOnHiding(event -> {
                try {
                    listMenu();
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba!");
                    alert.setHeaderText("Nem sikerült kapcsolódni az adatbázishoz");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void deleteBtnOnClick(ActionEvent actionEvent) {
        if (menuItem != null) {

            Alert confrimAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confrimAlert.setTitle("Megerősítő ablak");
            confrimAlert.setContentText("Biztosan ki szeretnéd törölni ezt az elemet?");

            Optional<ButtonType> result = confrimAlert.showAndWait();
            if (result.get() == ButtonType.OK){
                try {
                    menuService.delete(menuItem);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Sikeres törlés!");
                    alert.showAndWait();
                    listMenu();
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba");
                    alert.setHeaderText("Hiba történt a törlés során");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }

        }
    }

    @javafx.fxml.FXML
    public void changeIncreaseType(ActionEvent actionEvent) {
        if (increaseTypeInput.getValue() == "Ft") {
            increaseAmountInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(50,3000,50,50));
            increaseAmountInput.setPromptText("Ft");
        }
        else if (increaseTypeInput.getValue() == "%") {
            increaseAmountInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5,50,5,5));
            increaseAmountInput.setPromptText("%");
        }
    }

    @javafx.fxml.FXML
    public void increaseBtnOnClick(ActionEvent actionEvent) {
        Alert confrimAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confrimAlert.setTitle("Megerősítő ablak");
        if (menuItem != null) {
            confrimAlert.setContentText(String.format("Biztosan emelni szeretnéd %s termék árát?", menuItem.getName()));
            Optional<ButtonType> result = confrimAlert.showAndWait();
            if (result.get() == ButtonType.OK){
                try {
                    menuService.increasePrice(menuItem, (Integer) increaseAmountInput.getValue(), increaseTypeInput.getValue().toString());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Sikeres emelés!");
                    alert.showAndWait();
                    menuItem = null;
                    listMenu();
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba");
                    alert.setHeaderText("Hiba történt az emelés során");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        }
        else{
            confrimAlert.setContentText("Biztosan emelni szeretnéd az összes termék árát?");
            Optional<ButtonType> result = confrimAlert.showAndWait();
            if (result.get() == ButtonType.OK){
                try {
                    menuService.increasePrices((Integer) increaseAmountInput.getValue(), increaseTypeInput.getValue().toString());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Sikeres emelés!");
                    alert.showAndWait();
                    menuItem = null;
                    listMenu();
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba");
                    alert.setHeaderText("Hiba történt az emelés során");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        }

    }
}
