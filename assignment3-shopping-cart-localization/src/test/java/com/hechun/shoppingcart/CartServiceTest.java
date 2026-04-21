package com.hechun.shoppingcart;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartServiceTest {

    @Test
    void saveCartRecord_commitsWhenInsertsSucceed() throws Exception {
        CartService cartService = new CartService();
        CartItem item = new CartItem(1, 10.0, 2, 20.0);

        try (MockedStatic<DatabaseConnection> mockedDb = mockStatic(DatabaseConnection.class)) {
            Connection connection = mock(Connection.class);
            PreparedStatement recordStmt = mock(PreparedStatement.class);
            PreparedStatement itemStmt = mock(PreparedStatement.class);
            ResultSet keys = mock(ResultSet.class);

            mockedDb.when(DatabaseConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(contains("cart_records"), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(recordStmt);
            when(connection.prepareStatement(contains("cart_items"))).thenReturn(itemStmt);
            when(recordStmt.getGeneratedKeys()).thenReturn(keys);
            when(recordStmt.executeUpdate()).thenReturn(1);
            when(keys.next()).thenReturn(true);
            when(keys.getInt(1)).thenReturn(42);

            cartService.saveCartRecord(1, 20.0, "en_US", List.of(item));

            verify(recordStmt).setInt(1, 1);
            verify(recordStmt).setDouble(2, 20.0);
            verify(recordStmt).setString(3, "en_US");
            verify(itemStmt, times(1)).addBatch();
            verify(itemStmt).setInt(1, 42);
            verify(itemStmt).setInt(2, 1);
            verify(itemStmt).setDouble(3, 10.0);
            verify(itemStmt).setInt(4, 2);
            verify(itemStmt).setDouble(5, 20.0);
            verify(connection).commit();
        }
    }

    @Test
    void saveCartRecord_rollsBackWhenBatchThrows() throws Exception {
        CartService cartService = new CartService();
        CartItem item = new CartItem(1, 10.0, 2, 20.0);

        try (MockedStatic<DatabaseConnection> mockedDb = mockStatic(DatabaseConnection.class)) {
            Connection connection = mock(Connection.class);
            PreparedStatement recordStmt = mock(PreparedStatement.class);
            PreparedStatement itemStmt = mock(PreparedStatement.class);
            ResultSet keys = mock(ResultSet.class);

            mockedDb.when(DatabaseConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(contains("cart_records"), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(recordStmt);
            when(connection.prepareStatement(contains("cart_items"))).thenReturn(itemStmt);
            when(recordStmt.getGeneratedKeys()).thenReturn(keys);
            when(recordStmt.executeUpdate()).thenReturn(1);
            when(keys.next()).thenReturn(true);
            when(keys.getInt(1)).thenReturn(42);
            when(itemStmt.executeBatch()).thenThrow(new SQLException("batch error"));

            assertThrows(CartServiceException.class, () -> cartService.saveCartRecord(1, 20.0, "en_US", List.of(item)));

            verify(connection).rollback();
        }
    }

    @Test
    void saveCartRecord_rollsBackWhenGeneratedKeyMissing() throws Exception {
        CartService cartService = new CartService();
        CartItem item = new CartItem(1, 10.0, 2, 20.0);

        try (MockedStatic<DatabaseConnection> mockedDb = mockStatic(DatabaseConnection.class)) {
            Connection connection = mock(Connection.class);
            PreparedStatement recordStmt = mock(PreparedStatement.class);
            PreparedStatement itemStmt = mock(PreparedStatement.class);
            ResultSet keys = mock(ResultSet.class);

            mockedDb.when(DatabaseConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(contains("cart_records"), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(recordStmt);
            when(connection.prepareStatement(contains("cart_items"))).thenReturn(itemStmt);
            when(recordStmt.getGeneratedKeys()).thenReturn(keys);
            when(recordStmt.executeUpdate()).thenReturn(1);
            when(keys.next()).thenReturn(false);

            assertThrows(CartServiceException.class, () -> cartService.saveCartRecord(1, 20.0, "en_US", List.of(item)));

            verify(connection).rollback();
            verify(itemStmt, never()).addBatch();
        }
    }

    @Test
    void saveCartRecord_rollsBackWhenRecordInsertFails() throws Exception {
        CartService cartService = new CartService();
        CartItem item = new CartItem(1, 10.0, 2, 20.0);

        try (MockedStatic<DatabaseConnection> mockedDb = mockStatic(DatabaseConnection.class)) {
            Connection connection = mock(Connection.class);
            PreparedStatement recordStmt = mock(PreparedStatement.class);
            PreparedStatement itemStmt = mock(PreparedStatement.class);

            mockedDb.when(DatabaseConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(contains("cart_records"), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(recordStmt);
            when(connection.prepareStatement(contains("cart_items"))).thenReturn(itemStmt);
            when(recordStmt.executeUpdate()).thenThrow(new SQLException("record insert failed"));

            assertThrows(RuntimeException.class, () -> cartService.saveCartRecord(1, 20.0, "en_US", List.of(item)));

            verify(connection).rollback();
            verify(itemStmt, never()).addBatch();
        }
    }

    @Test
    void saveCartRecord_wrapsConnectionFailure() throws Exception {
        CartService cartService = new CartService();

        try (MockedStatic<DatabaseConnection> mockedDb = mockStatic(DatabaseConnection.class)) {
            mockedDb.when(DatabaseConnection::getConnection).thenThrow(new SQLException("boom"));

            assertThrows(CartServiceException.class, () -> cartService.saveCartRecord(0, 0.0, "en_US", List.of()));
        }
    }
}
