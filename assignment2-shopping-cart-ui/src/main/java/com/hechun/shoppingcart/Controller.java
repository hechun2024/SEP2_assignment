package com.hechun.shoppingcart;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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

    private ResourceBundle bundle;
    private Locale currentLocale = Locale.US;
    private final List<TextField> priceFields = new ArrayList<>();
    private final List<TextField> quantityFields = new ArrayList<>();
    private final List<Label> itemTotalLabels = new ArrayList<>();

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
        bundle = ResourceBundle.getBundle("MessagesBundle", currentLocale);

        selectLanguageLabel.setText(getSafe("select.language", "Select the language:"));
        confirmLanguageButton.setText(getSafe("confirm.language", "Confirm Language"));
        enterItemsLabel.setText(getSafe("enter.number.of.items", "Enter number of items:"));
        enterItemsButton.setText(getSafe("enter.items", "Enter Items"));
        calculateButton.setText(getSafe("calculate.total", "Calculate Total"));
        resultLabel.setText(getSafe("total", "Total:"));

        applyTextDirection(currentLocale);
    }

    private void applyTextDirection(Locale locale) {
        boolean rtl = "ar".equalsIgnoreCase(locale.getLanguage());
        NodeOrientation orientation = rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT;

        // 1. 设置根级容器的镜像方向
        rootPane.setNodeOrientation(orientation);
        mainBox.setNodeOrientation(orientation);
        itemsBox.setNodeOrientation(orientation);

        // 2. 设置容器的对齐方式
        Pos vBoxAlignment = rtl ? Pos.TOP_RIGHT : Pos.TOP_LEFT;
        mainBox.setAlignment(vBoxAlignment);
        itemsBox.setAlignment(vBoxAlignment);

        // 3. 处理所有控件的对齐
        Pos elementAlignment = rtl ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT;
        TextAlignment textAlign = rtl ? TextAlignment.RIGHT : TextAlignment.LEFT;

        // 处理静态标签
        setupControl(selectLanguageLabel, elementAlignment, textAlign);
        setupControl(enterItemsLabel, elementAlignment, textAlign);
        setupControl(resultLabel, elementAlignment, textAlign);

        // 处理按钮
        setupControl(confirmLanguageButton, elementAlignment, textAlign);
        setupControl(enterItemsButton, elementAlignment, textAlign);
        setupControl(calculateButton, elementAlignment, textAlign);

        // 处理文本输入框
        setupTextField(numItemsField, elementAlignment);

        // 处理 ComboBox
        setupComboBox(languageBox, elementAlignment);

        // 4. 处理 itemsBox 中的动态生成的组件
        for (Node node : itemsBox.getChildren()) {
            if (node instanceof Label label) {
                setupControl(label, elementAlignment, textAlign);
            } else if (node instanceof TextField tf) {
                setupTextField(tf, elementAlignment);
            }
            // Separator 在 RTL 模式下会自动镜像，不需要特殊处理
        }
    }

    /**
     * 配置 Label 和 Button 的对齐和文本方向
     */
    private void setupControl(Labeled control, Pos alignment, TextAlignment textAlignment) {
        control.setMaxWidth(Double.MAX_VALUE);
        control.setAlignment(alignment);
        if (control instanceof Label label) {
            label.setTextAlignment(textAlignment);
        }
    }

    /**
     * 配置 TextField 的对齐
     */
    private void setupTextField(TextField field, Pos alignment) {
        field.setMaxWidth(Double.MAX_VALUE);
        field.setAlignment(alignment);
    }

    /**
     * 配置 ComboBox 的对齐
     */
    private void setupComboBox(ComboBox<?> comboBox, @SuppressWarnings("unused") Pos alignment) {
        comboBox.setMaxWidth(Double.MAX_VALUE);
        // ComboBox 在 RTL 模式下会自动调整方向
    }

    private String getSafe(String key, String defaultText) {
        try { return bundle.getString(key); } catch (Exception e) { return defaultText; }
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
            if (n <= 0) throw new Exception();
        } catch (Exception e) {
            resultLabel.setText(getSafe("total", "Total:") + " Invalid number");
            return;
        }

        for (int i = 0; i < n; i++) {
            Label priceLabel = new Label(getSafe("price", "Price") + " " + (i + 1));
            TextField priceField = new TextField();
            priceField.setPromptText(getSafe("enter.price", "Enter price:"));

            Label quantityLabel = new Label(getSafe("quantity", "Quantity") + " " + (i + 1));
            TextField quantityField = new TextField();
            quantityField.setPromptText(getSafe("enter.quantity", "Enter quantity:"));

            Label itemTotalLabel = new Label(buildItemTotalText(i + 1, 0.0));

            itemsBox.getChildren().addAll(priceLabel, priceField, quantityLabel, quantityField, itemTotalLabel);

            priceFields.add(priceField);
            quantityFields.add(quantityField);
            itemTotalLabels.add(itemTotalLabel);
        }
        applyTextDirection(currentLocale);
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
        for (int i = 0; i < priceFields.size(); i++) {
            priceFields.get(i).setPromptText(getSafe("enter.price", "Enter price:"));
            quantityFields.get(i).setPromptText(getSafe("enter.quantity", "Enter quantity:"));
            itemTotalLabels.get(i).setText(buildItemTotalText(i + 1, 0.0));
        }
        applyTextDirection(currentLocale);
    }

    private String buildItemTotalText(int itemNumber, double value) {
        return getSafe("item", "Item") + " " + itemNumber + " - "
                + getSafe("item.total", "Total:") + " " + value;
    }
}