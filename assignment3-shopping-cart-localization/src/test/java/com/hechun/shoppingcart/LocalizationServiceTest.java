package com.hechun.shoppingcart;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class LocalizationServiceTest {

    @Test
    void getStrings_returnsMapFromDatabase() throws Exception {
        LocalizationService service = new LocalizationService();

        try (MockedStatic<DatabaseConnection> mockedDb = mockStatic(DatabaseConnection.class)) {
            Connection connection = mock(Connection.class);
            PreparedStatement statement = mock(PreparedStatement.class);
            ResultSet resultSet = mock(ResultSet.class);

            mockedDb.when(DatabaseConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true, true, false);
            when(resultSet.getString("key")).thenReturn("greeting", "farewell");
            when(resultSet.getString("value")).thenReturn("Hello", "Bye");

            Map<String, String> strings = service.getStrings("en_US");

            assertEquals(2, strings.size());
            assertEquals("Hello", strings.get("greeting"));
            assertEquals("Bye", strings.get("farewell"));
        }
    }

    @Test
    void getStrings_handlesSQLExceptionGracefully() throws Exception {
        LocalizationService service = new LocalizationService();

        try (MockedStatic<DatabaseConnection> mockedDb = mockStatic(DatabaseConnection.class)) {
            Connection connection = mock(Connection.class);

            mockedDb.when(DatabaseConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenThrow(new SQLException("boom"));

            Map<String, String> strings = service.getStrings("en_US");

            assertTrue(strings.isEmpty());
        }
    }
}
