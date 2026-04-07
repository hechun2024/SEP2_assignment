package com.hechun.shoppingcart;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Controller {

    @FXML
    private ScrollPane rootPane;
    @FXML
    private VBox mainBox;
    @FXML
    private ComboBox<String> languageBox;
    @FXML
    private Label selectLanguageLabel;
    @FXML
    private Button confirmLanguageButton;
    @FXML
    private Label enterItemsLabel;
    @FXML
    private Button enterItemsButton;
    @FXML
    private Button calculateButton;
    @FXML
    private TextField numItemsField;
    @FXML
    private VBox itemsBox;
    @FXML
    private Label resultLabel;

    private final LocalizationService localizationService = new LocalizationService();
    private final CartService cartService = new CartService();
    private Map<String, String> messages = new HashMap<>();

    private Locale currentLocale = Locale.US;
    private String currentLanguage = "en_US";

    private final List<TextField> priceFields = new ArrayList<>();
    private final List<TextField> quantityFields = new ArrayList<>();
    private final List<Label> itemTotalLabels = new ArrayList<>();
    private double cartTotal = 0.0;

    @FXML
    public void initialize() {
        languageBox.getItems().addAll("English", "Finnish", "Swedish", "Japanese", "Arabic");
        languageBox.setValue("English");
        mainBox.setFillWidth(true);
        itemsBox.setFillWidth(true);

        loadLanguage("en", "US");
    }

    @FXML
    private void switchLanguage() {
        String lang = languageBox.getValue();

        switch (lang) {
            case "Finnish" -> loadLanguage("fi", "FI");
            case "Swedish" -> loadLanguage("sv", "SE");
            case "Japanese" -> loadLanguage("ja", "JP");
            case "Arabic" -> loadLanguage("ar", "AR");
            default -> loadLanguage("en", "US");
        }

        refreshDynamicFieldsLanguage();
    }

    private void loadLanguage(String lang, String country) {
        currentLocale = new Locale(lang, country);
        currentLanguage = lang + "_" + country;

        messages = localizationService.getStrings(currentLanguage);

        // 如果数据库里没有对应语言，回退到英文
        if (messages.isEmpty()) {
            System.err.println("[Controller] No strings found for " + currentLanguage + ", falling back to en_US");
            currentLanguage = "en_US";
            currentLocale = Locale.US;
            messages = localizationService.getStrings(currentLanguage);
        }

        selectLanguageLabel.setText(msg("select.language", "Select the language:"));
        confirmLanguageButton.setText(msg("confirm.language", "Confirm Language"));
        enterItemsLabel.setText(msg("enter.number.of.items", "Enter number of items:"));
        enterItemsButton.setText(msg("enter.items", "Enter Items"));
        calculateButton.setText(msg("calculate.total", "Calculate Total"));
        resultLabel.setText(msg("total", "Total:"));

        applyTextDirection(currentLocale);
    }

    private String msg(String key, String defaultText) {
        return messages.getOrDefault(key, defaultText);
    }

    private void applyTextDirection(Locale locale) {
        boolean rtl = "ar".equalsIgnoreCase(locale.getLanguage());
        NodeOrientation orientation = rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT;

        rootPane.setNodeOrientation(orientation);
        mainBox.setNodeOrientation(orientation);
        itemsBox.setNodeOrientation(orientation);

        Pos vBoxAlignment = rtl ? Pos.TOP_RIGHT : Pos.TOP_LEFT;
        mainBox.setAlignment(vBoxAlignment);
        itemsBox.setAlignment(vBoxAlignment);

        Pos elementAlignment = rtl ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT;
        TextAlignment textAlign = rtl ? TextAlignment.RIGHT : TextAlignment.LEFT;

        setupControl(selectLanguageLabel, elementAlignment, textAlign);
        setupControl(enterItemsLabel, elementAlignment, textAlign);
        setupControl(resultLabel, elementAlignment, textAlign);

        setupControl(confirmLanguageButton, elementAlignment, textAlign);
        setupControl(enterItemsButton, elementAlignment, textAlign);
        setupControl(calculateButton, elementAlignment, textAlign);

        setupTextField(numItemsField, elementAlignment);
        setupComboBox(languageBox, elementAlignment);

        for (Node node : itemsBox.getChildren()) {
            if (node instanceof Label label) {
                setupControl(label, elementAlignment, textAlign);
            } else if (node instanceof TextField tf) {
                setupTextField(tf, elementAlignment);
            }
        }
    }

    private void setupControl(Labeled control, Pos alignment, TextAlignment textAlignment) {
        control.setMaxWidth(Double.MAX_VALUE);
        control.setAlignment(alignment);

        if (control instanceof Label label) {
            label.setTextAlignment(textAlignment);
        }
    }

    private void setupTextField(TextField field, Pos alignment) {
        field.setMaxWidth(Double.MAX_VALUE);
        field.setAlignment(alignment);
    }

    private void setupComboBox(ComboBox<?> comboBox, @SuppressWarnings("unused") Pos alignment) {
        comboBox.setMaxWidth(Double.MAX_VALUE);
    }

    @FXML
    private void handleItems() {
        itemsBox.getChildren().clear();
        priceFields.clear();
        quantityFields.clear();
        itemTotalLabels.clear();

        int n;
        try {
            n = Integer.parseInt(numItemsField.getText().trim());
            if (n <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            resultLabel.setText(msg("total", "Total:") + " Invalid number");
            return;
        }

        for (int i = 0; i < n; i++) {
            Label priceLabel = new Label(msg("price", "Price") + " " + (i + 1));
            TextField priceField = new TextField();
            priceField.setPromptText(msg("enter.price", "Enter price:"));

            Label quantityLabel = new Label(msg("quantity", "Quantity") + " " + (i + 1));
            TextField quantityField = new TextField();
            quantityField.setPromptText(msg("enter.quantity", "Enter quantity:"));

            Label itemTotalLabel = new Label(buildItemTotalText(i + 1, 0.0));

            itemsBox.getChildren().addAll(
                    priceLabel, priceField,
                    quantityLabel, quantityField,
                    itemTotalLabel
            );

            priceFields.add(priceField);
            quantityFields.add(quantityField);
            itemTotalLabels.add(itemTotalLabel);
        }

        applyTextDirection(currentLocale);
    }

    @FXML
    private void handleCalculate() {
        double total = 0;
        List<CartItem> items = new ArrayList<>();

        try {
            for (int i = 0; i < priceFields.size(); i++) {
                double price = Double.parseDouble(priceFields.get(i).getText().trim());
                int qty = Integer.parseInt(quantityFields.get(i).getText().trim());

                double itemTotal = price * qty;
                total += itemTotal;

                items.add(new CartItem(i + 1, price, qty, itemTotal));
                itemTotalLabels.get(i).setText(buildItemTotalText(i + 1, itemTotal));
            }

            cartTotal = total;
            resultLabel.setText(msg("total", "Total:") + " " + total);

            // Save cart record to database
            if (items.size() > 0) {
                cartService.saveCartRecord(items.size(), total, currentLanguage, items);
            }

        } catch (Exception e) {
            resultLabel.setText(msg("total", "Total:") + " Error");
        }
    }

    private void refreshDynamicFieldsLanguage() {
        for (int i = 0; i < priceFields.size(); i++) {
            quantityFields.get(i).setPromptText(msg("enter.quantity", "Enter quantity:"));
            priceFields.get(i).setPromptText(msg("enter.price", "Enter price:"));
            itemTotalLabels.get(i).setText(buildItemTotalText(i + 1, 0.0));
        }

        applyTextDirection(currentLocale);
    }

    private String buildItemTotalText(int itemNumber, double value) {
        return msg("item", "Item") + " " + itemNumber + " - "
                + msg("item.total", "Total:") + " " + value;
    }
}