package com.hechun.shoppingcart;

import java.sql.*;
import java.util.List;

public class CartService {

    public void saveCartRecord(int totalItems, double totalCost, String language, List<CartItem> items) {
        String insertCartRecord = "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";
        String insertCartItem = "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (
                    PreparedStatement cartRecordStmt = connection.prepareStatement(insertCartRecord, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement cartItemStmt = connection.prepareStatement(insertCartItem)
            ) {
                // 1. 保存主记录
                cartRecordStmt.setInt(1, totalItems);
                cartRecordStmt.setDouble(2, totalCost);
                cartRecordStmt.setString(3, language);
                cartRecordStmt.executeUpdate();

                int cartRecordId = -1;
                try (ResultSet keys = cartRecordStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        cartRecordId = keys.getInt(1);
                    }
                }

                if (cartRecordId == -1) {
                    throw new SQLException("Failed to get cart_record id.");
                }

                // 2. 保存每个 item
                for (CartItem item : items) {
                    cartItemStmt.setInt(1, cartRecordId);
                    cartItemStmt.setInt(2, item.getItemNumber());
                    cartItemStmt.setDouble(3, item.getPrice());
                    cartItemStmt.setInt(4, item.getQuantity());
                    cartItemStmt.setDouble(5, item.getSubtotal());
                    cartItemStmt.addBatch();
                }

                cartItemStmt.executeBatch();
                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}