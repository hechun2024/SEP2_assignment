package com.hechun.shoppingcart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class LocalizationService {

    public Map<String, String> getStrings(String language) {
        Map<String, String> localizedStrings = new HashMap<>();

        String sql = "SELECT `key`, value FROM localization_strings WHERE language = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, language);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    localizedStrings.put(rs.getString("key"), rs.getString("value"));
                }
            }

            System.out.println("[LocalizationService] Loaded " + localizedStrings.size() + " strings for language: " + language);

        } catch (Exception e) {
            System.err.println("[LocalizationService] Error loading strings for language: " + language);
            e.printStackTrace();
        }

        return localizedStrings;
    }
}