package com.hechun.shoppingcart;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class DatabaseConnectionTest {

    @Test
    void getConnection_requiresPassword() {
        DatabaseConnection.setEnvironmentProvider(key -> {
            if ("MARIADB_PASSWORD".equals(key)) {
                return null;
            }
            return "x";
        });

        try {
            assertThrows(IllegalStateException.class, DatabaseConnection::getConnection);
        } finally {
            DatabaseConnection.resetEnvironmentProvider();
        }
    }

    @Test
    void getConnection_rejectsBlankPassword() {
        DatabaseConnection.setEnvironmentProvider(key -> {
            if ("MARIADB_PASSWORD".equals(key)) {
                return "  ";
            }
            return "x";
        });

        try {
            assertThrows(IllegalStateException.class, DatabaseConnection::getConnection);
        } finally {
            DatabaseConnection.resetEnvironmentProvider();
        }
    }

    @Test
    void getConnection_usesEnvironmentValues() throws SQLException {
        DatabaseConnection.setEnvironmentProvider(key -> {
            return switch (key) {
                case "MARIADB_URL" -> "jdbc:url";
                case "MARIADB_USER" -> "user";
                case "MARIADB_PASSWORD" -> "secret";
                default -> null;
            };
        });

        try (MockedStatic<DriverManager> drivers = mockStatic(DriverManager.class)) {
            Connection connection = mock(Connection.class);
            drivers.when(() -> DriverManager.getConnection("jdbc:url", "user", "secret")).thenReturn(connection);

            assertSame(connection, DatabaseConnection.getConnection());
        } finally {
            DatabaseConnection.resetEnvironmentProvider();
        }
    }

    @Test
    void getConnection_fallsBackToDefaults() throws SQLException {
        DatabaseConnection.setEnvironmentProvider(key -> {
            if ("MARIADB_PASSWORD".equals(key)) {
                return "secret";
            }
            return null;
        });

        try (MockedStatic<DriverManager> drivers = mockStatic(DriverManager.class)) {
            Connection connection = mock(Connection.class);
            drivers.when(() -> DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/shopping_cart_localization",
                    "shopuser", "secret")).thenReturn(connection);

            assertSame(connection, DatabaseConnection.getConnection());
        } finally {
            DatabaseConnection.resetEnvironmentProvider();
        }
    }
}
