package com.hechun.shoppingcart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:mariadb://localhost:3306/shopping_cart_localization";
    private static final String DEFAULT_USER = "shopuser";
    private static EnvironmentProvider environmentProvider = System::getenv;

    public static Connection getConnection() throws SQLException {
        String url = getUrl();
        String user = getUser();
        String password = getPassword();

        if (password == null || password.isBlank()) {
            throw new IllegalStateException("Missing env var: MARIADB_PASSWORD");
        }

        return DriverManager.getConnection(url, user, password);
    }

    static String getUrl() {
        String url = environmentProvider.getenv("MARIADB_URL");
        return url != null ? url : DEFAULT_URL;
    }

    static String getUser() {
        String user = environmentProvider.getenv("MARIADB_USER");
        return user != null ? user : DEFAULT_USER;
    }

    static String getPassword() {
        return environmentProvider.getenv("MARIADB_PASSWORD");
    }

    static void setEnvironmentProvider(EnvironmentProvider provider) {
        environmentProvider = provider != null ? provider : System::getenv;
    }

    static void resetEnvironmentProvider() {
        environmentProvider = System::getenv;
    }

    interface EnvironmentProvider {
        String getenv(String key);
    }

}
