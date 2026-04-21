package com.hechun.shoppingcart;

import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock
    private CartService cartService;

    private Controller controller;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {
                // JavaFX toolkit must initialize, nothing to run here.
            });
        } catch (IllegalStateException ignored) {
            // Toolkit already started; nothing to do.
        }
    }

    @BeforeEach
    void setUp() {
        controller = new Controller();
        injectField("rootPane", new ScrollPane());
        injectField("mainBox", new VBox());
        injectField("itemsBox", new VBox());
        injectField("languageBox", new ComboBox<>());
        injectField("selectLanguageLabel", new Label());
        injectField("confirmLanguageButton", new Button());
        injectField("enterItemsLabel", new Label());
        injectField("enterItemsButton", new Button());
        injectField("calculateButton", new Button());
        injectField("numItemsField", new TextField());
        injectField("resultLabel", new Label());
        injectField("cartService", cartService);
    }

    @Test
    void handleItems_withPositiveNumber_createsFieldsAndPrompts() {
        setNumItems("2");

        invokeHandleItems();

        List<TextField> priceFields = getListField("priceFields");
        List<TextField> quantityFields = getListField("quantityFields");
        List<Label> totalLabels = getListField("itemTotalLabels");

        assertEquals(2, priceFields.size());
        assertEquals(2, quantityFields.size());
        assertEquals(2, totalLabels.size());
        assertTrue(totalLabels.stream().allMatch(label -> label.getText().contains("Total:")));
        assertEquals("", getResultText());
    }

    @Test
    void handleItems_withInvalidNumber_setsInvalidMessage() {
        setNumItems("0");

        invokeHandleItems();

        assertEquals("Total: Invalid number", getResultText());
        assertTrue(getItemsBox().getChildren().isEmpty());
    }

    @Test
    void handleCalculate_success_updatesTotalsAndSavesRecord() {
        setNumItems("2");
        invokeHandleItems();

        List<TextField> priceFields = getListField("priceFields");
        List<TextField> quantityFields = getListField("quantityFields");

        priceFields.get(0).setText("10");
        quantityFields.get(0).setText("2");
        priceFields.get(1).setText("5");
        quantityFields.get(1).setText("3");

        ArgumentCaptor<List<CartItem>> captor = ArgumentCaptor.forClass(List.class);

        invokeHandleCalculate();

        verify(cartService).saveCartRecord(eq(2), eq(35.0), eq("en_US"), captor.capture());
        List<CartItem> savedItems = captor.getValue();
        assertEquals(2, savedItems.size());
        assertEquals("Total: 35.0", getResultText());
    }

    @Test
    void handleCalculate_withParsingError_setsErrorAndSkipsSave() {
        setNumItems("1");
        invokeHandleItems();

        List<TextField> priceFields = getListField("priceFields");
        List<TextField> quantityFields = getListField("quantityFields");

        priceFields.get(0).setText("not-a-number");
        quantityFields.get(0).setText("1");

        invokeHandleCalculate();

        assertEquals("Total: Error", getResultText());
        verify(cartService, never()).saveCartRecord(anyInt(), anyDouble(), anyString(), anyList());
    }

    @Test
    void loadLanguage_usesLocalizedStrings_whenAvailable() {
        Map<String, String> localized = Map.of(
                "select.language", "Valitse kieli",
                "confirm.language", "Vahvista",
                "total", "Yhteensä"
        );

        withLocalizationSequences(List.of(localized), () -> {
            invokeLoadLanguage("fi", "FI");

            Label selectLabel = (Label) getFieldValue("selectLanguageLabel");
            Button confirmButton = (Button) getFieldValue("confirmLanguageButton");
            Label resultLabel = (Label) getFieldValue("resultLabel");

            assertEquals("Valitse kieli", selectLabel.getText());
            assertEquals("Vahvista", confirmButton.getText());
            assertEquals("Yhteensä", resultLabel.getText());
        });
    }

    @Test
    void loadLanguage_fallsBackToEnglish_whenStringsMissing() {
        Map<String, String> english = Map.of(
                "select.language", "Select language:",
                "total", "Total:"
        );

        withLocalizationSequences(List.of(Map.of(), english), () -> {
            invokeLoadLanguage("sv", "SE");

            assertEquals("Select language:", ((Label) getFieldValue("selectLanguageLabel")).getText());
            assertEquals("en_US", getCurrentLanguage());
        });
    }

    @Test
    void switchLanguage_toArabic_appliesRightToLeftDirection() {
        Map<String, String> arabic = Map.of(
                "select.language", "اختر اللغة",
                "confirm.language", "تأكيد",
                "total", "الإجمالي"
        );

        withLocalizationSequences(List.of(arabic), () -> {
            setLanguageBoxValue("Arabic");
            invokeSwitchLanguage();

            ScrollPane rootPane = (ScrollPane) getFieldValue("rootPane");
            assertEquals(NodeOrientation.RIGHT_TO_LEFT, rootPane.getNodeOrientation());
            assertEquals("اختر اللغة", ((Label) getFieldValue("selectLanguageLabel")).getText());
        });
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getListField(String name) {
        return (List<T>) getFieldValue(name);
    }

    private String getResultText() {
        Label resultLabel = (Label) getFieldValue("resultLabel");
        return resultLabel.getText();
    }

    private VBox getItemsBox() {
        return (VBox) getFieldValue("itemsBox");
    }

    private void setNumItems(String text) {
        TextField field = (TextField) getFieldValue("numItemsField");
        field.setText(text);
    }

    private void invokeHandleItems() {
        invokeControllerMethod("handleItems");
    }

    private void invokeHandleCalculate() {
        invokeControllerMethod("handleCalculate");
    }

    private void invokeControllerMethod(String name, Object... args) {
        try {
            Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
            Method method = Controller.class.getDeclaredMethod(name, paramTypes);
            method.setAccessible(true);
            method.invoke(controller, args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void invokeLoadLanguage(String lang, String country) {
        invokeControllerMethod("loadLanguage", lang, country);
    }

    private void invokeSwitchLanguage() {
        invokeControllerMethod("switchLanguage");
    }

    private void setLanguageBoxValue(String value) {
        @SuppressWarnings("unchecked")
        ComboBox<String> languageBox = (ComboBox<String>) getFieldValue("languageBox");
        languageBox.setValue(value);
    }

    private String getCurrentLanguage() {
        return (String) getFieldValue("currentLanguage");
    }

    private void withLocalizationSequences(List<Map<String, String>> sequences, Runnable action) {
        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {
            Connection connection = mock(Connection.class);
            mocked.when(DatabaseConnection::getConnection).thenReturn(connection);

            PreparedStatement[] statements = new PreparedStatement[sequences.size()];
            ResultSet[] results = new ResultSet[sequences.size()];
            for (int i = 0; i < sequences.size(); i++) {
                statements[i] = mock(PreparedStatement.class);
            results[i] = mock(ResultSet.class);
                try {
                    when(statements[i].executeQuery()).thenReturn(results[i]);
                } catch (SQLException e) {
                    throw new IllegalStateException(e);
                }
            try {
                configureResultSet(results[i], sequences.get(i));
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
            }

            AtomicInteger counter = new AtomicInteger(0);
            try {
                when(connection.prepareStatement(anyString())).thenAnswer(invocation -> {
                int index = counter.getAndIncrement();
                if (index >= statements.length) {
                    throw new AssertionError("Unexpected prepareStatement call: " + index);
                }
                return statements[index];
                });
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }

            action.run();
        }
    }

    private void configureResultSet(ResultSet resultSet, Map<String, String> data) throws SQLException {
        List<Map.Entry<String, String>> entries = new ArrayList<>(data.entrySet());
        if (entries.isEmpty()) {
            when(resultSet.next()).thenReturn(false);
            return;
        }
        AtomicInteger cursor = new AtomicInteger(-1);
        when(resultSet.next()).thenAnswer(invocation -> cursor.incrementAndGet() < entries.size());
        when(resultSet.getString("key")).thenAnswer(invocation -> entries.get(cursor.get()).getKey());
        when(resultSet.getString("value")).thenAnswer(invocation -> entries.get(cursor.get()).getValue());
    }

    private void injectField(String name, Object value) {
        try {
            Field field = Controller.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(controller, value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Object getFieldValue(String name) {
        try {
            Field field = Controller.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(controller);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
