package com.example.etlap;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

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

    public void initialize() {
        name.setCellValueFactory(new PropertyValueFactory<Menu, String>("name"));
        price.setCellValueFactory(new PropertyValueFactory<Menu, Integer>("price"));
        category.setCellValueFactory(new PropertyValueFactory<Menu, String>("category"));

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


    @Deprecated
    public void addBtnOnClick(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("addMenuItem.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
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
