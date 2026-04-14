package com.hechun.shoppingcart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalizationService {

    private static final Logger LOGGER = Logger.getLogger(LocalizationService.class.getName());

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

            LOGGER.log(Level.INFO, "Loaded {0} strings for language: {1}", new Object[]{localizedStrings.size(), language});

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e, () -> "Error loading strings for language: " + language);
        }

        return localizedStrings;
    }
}
