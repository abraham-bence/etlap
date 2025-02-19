package com.example.etlap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


public class AddMenuItem {
    @javafx.fxml.FXML
    private Spinner priceInput;
    @javafx.fxml.FXML
    private TextField descriptionInput;
    @javafx.fxml.FXML
    private TextField nameInput;

    private MenuService menuService;
    @FXML
    private ChoiceBox categoryInput;

    public void initialize() {
        priceInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,10000000, 100000, 1000));
        categoryInput.setItems(FXCollections.observableArrayList(Category.values()));
    }

    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }


    @javafx.fxml.FXML
    public void addButtonClick(ActionEvent actionEvent) {
        //Adatvalidáció
        String name = nameInput.getText();
        Category category = Category.valueOf(categoryInput.getValue().toString());
        int price = (int) priceInput.getValue();
        String description = descriptionInput.getText();
        Menu menu = new Menu(null, name, description, price, category);
        try {
            menuService.create(menu);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Sikeres felvétel!");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText("Hiba történt a felvétel során");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
