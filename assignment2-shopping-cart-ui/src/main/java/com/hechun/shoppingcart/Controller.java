package com.hechun.shoppingcart;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class Controller {

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

    private ResourceBundle bundle;

    private final List<TextField> priceFields = new ArrayList<>();
    private final List<TextField> quantityFields = new ArrayList<>();
    private final List<Label> itemTotalLabels = new ArrayList<>();

    @FXML
    public void initialize() {
        languageBox.getItems().addAll("English", "Finnish", "Swedish", "Japanese", "Arabic");
        languageBox.setValue("English");
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

    private String getSafe(String key, String defaultText) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return defaultText;
        }
    }

    private void loadLanguage(String lang, String country) {
        bundle = ResourceBundle.getBundle("MessagesBundle", new Locale(lang, country));

        selectLanguageLabel.setText(getSafe("select.language", "Select the language:"));
        confirmLanguageButton.setText(getSafe("confirm.language", "Confirm Language"));
        enterItemsLabel.setText(getSafe("enter.number.of.items", "Enter number of items:"));
        enterItemsButton.setText(getSafe("enter.items", "Enter Items"));
        calculateButton.setText(getSafe("calculate.total", "Calculate Total"));
        resultLabel.setText(getSafe("total", "Total:"));
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
                resultLabel.setText(getSafe("total", "Total:") + " Invalid number");
                return;
            }
        } catch (Exception e) {
            resultLabel.setText(getSafe("total", "Total:") + " Invalid number");
            return;
        }

        for (int i = 0; i < n; i++) {
            Label priceLabel = new Label(getSafe("price", "Price") + " " + (i + 1));
            TextField priceField = new TextField();
            priceField.setPromptText(getSafe("enter.price", "Enter price for item:"));

            Label quantityLabel = new Label(getSafe("quantity", "Quantity") + " " + (i + 1));
            TextField quantityField = new TextField();
            quantityField.setPromptText(getSafe("enter.quantity", "Enter quantity for item:"));

            Label itemTotalLabel = new Label(buildItemTotalText(i + 1, 0.0));

            itemsBox.getChildren().addAll(
                    priceLabel,
                    priceField,
                    quantityLabel,
                    quantityField,
                    itemTotalLabel
            );

            priceFields.add(priceField);
            quantityFields.add(quantityField);
            itemTotalLabels.add(itemTotalLabel);
        }

        resultLabel.setText(getSafe("total", "Total:"));
    }

    @FXML
    private void handleCalculate() {
        double total = 0;

        try {
            for (int i = 0; i < priceFields.size(); i++) {
                double price = Double.parseDouble(priceFields.get(i).getText().trim());
                int qty = Integer.parseInt(quantityFields.get(i).getText().trim());

                double itemTotal = price * qty;
                total += itemTotal;

                itemTotalLabels.get(i).setText(buildItemTotalText(i + 1, itemTotal));
            }

            resultLabel.setText(getSafe("total", "Total:") + " " + total);

        } catch (Exception e) {
            resultLabel.setText(getSafe("total", "Total:") + " Error");
        }
    }

    private void refreshDynamicFieldsLanguage() {
        int index = 0;

        for (int i = 0; i < priceFields.size(); i++) {
            Label priceLabel = (Label) itemsBox.getChildren().get(index);
            TextField priceField = (TextField) itemsBox.getChildren().get(index + 1);
            Label quantityLabel = (Label) itemsBox.getChildren().get(index + 2);
            TextField quantityField = (TextField) itemsBox.getChildren().get(index + 3);
            Label itemTotalLabel = (Label) itemsBox.getChildren().get(index + 4);

            priceLabel.setText(getSafe("price", "Price") + " " + (i + 1));
            priceField.setPromptText(getSafe("enter.price", "Enter price for item:"));

            quantityLabel.setText(getSafe("quantity", "Quantity") + " " + (i + 1));
            quantityField.setPromptText(getSafe("enter.quantity", "Enter quantity for item:"));

            double currentValue = 0.0;
            itemTotalLabel.setText(buildItemTotalText(i + 1, currentValue));

            index += 5;
        }

        resultLabel.setText(getSafe("total", "Total:"));
    }

    private String buildItemTotalText(int itemNumber, double value) {
        return getSafe("item", "Item") + " " + itemNumber + " - "
                + getSafe("item.total", "Total:") + " " + value;
    }
}