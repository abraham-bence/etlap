package com.example.etlap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuService {
    private static final String DB_DRIVER = "mysql";
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_DATABASE = "etlap";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private final Connection connection;

    public MenuService() throws SQLException {
        String dbUrl = String.format("jdbc:%s://%s:%s/%s", "mysql", "localhost", "3306", "etlapdb");
        this.connection = DriverManager.getConnection(dbUrl, "root", "");
    }

    public List<Menu> getAll() throws SQLException {
        List<Menu> menu = new ArrayList();
        Statement statement = this.connection.createStatement();
        String sql = "SELECT * FROM etlap";
        ResultSet result = statement.executeQuery(sql);

        while(result.next()) {
            int id = result.getInt("id");
            String name = result.getString("nev");
            String description = result.getString("leiras");
            int price = result.getInt("ar");
            Category category = Category.valueOf(result.getString("kategoria"));
            menu.add(new Menu( id, name, description, price, category));
        }

        return menu;
    }

    public boolean create(Menu menu) throws SQLException {
        String sql = "INSERT INTO etlap(nev, leiras, ar, kategoria) VALUES(?,?,?,?);";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, menu.getName());
        statement.setString(2, menu.getDescription());
        statement.setInt(3, menu.getPrice());
        statement.setString(4, menu.getCategory().toString());
        return statement.executeUpdate() == 1;
    }

    public boolean delete(Menu menuItem) throws SQLException {
        String sql = "DELETE FROM etlap WHERE id = ?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, menuItem.getId().toString());

        return statement.executeUpdate() == 1;
    }

    public boolean increasePrice(Menu menuItem, int amount, String type) throws SQLException {
        int newPrice = 0;
        if(type == "Ft") {
            newPrice = menuItem.getPrice() + amount;
        }
        else if(type == "%") {
            double percent = (double) amount / 100;
            double percentOfprice = percent * menuItem.getPrice();
            newPrice = (int) (menuItem.getPrice() + percentOfprice);
        }
        String sql = "UPDATE etlap SET ar = ? WHERE id = ?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,newPrice);
        statement.setInt(2, menuItem.getId());

        return statement.executeUpdate() == 1;
    }

    public void increasePrices(int amount, String type) throws SQLException {
        List<Menu> menu = getAll();
        for(Menu menuItem : menu) {
            increasePrice(menuItem, amount, type);
        }
    }
}
