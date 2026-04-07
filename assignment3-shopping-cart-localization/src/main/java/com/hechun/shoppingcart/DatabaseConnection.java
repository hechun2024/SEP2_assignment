package com.hechun.shoppingcart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mariadb://localhost:3306/shopping_cart_localization";
    private static final String USER = "shopuser";
    private static final String PASSWORD = "123456";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        System.out.println("Testing database connection...");

        try (Connection conn = getConnection()) {
            System.out.println("Connected to database successfully!");
            System.out.println("Catalog = " + conn.getCatalog());
            System.out.println("Is valid = " + conn.isValid(2));
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}