package com.hechun.shoppingcart;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        Locale locale = LanguageSelector.selectLocale(scanner);
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", locale);

        CartCalculator calculator = new CartCalculator();
        List<Double> itemTotals = new ArrayList<>();

        System.out.print(messages.getString("enter.number.of.items"));
        int itemCount = scanner.nextInt();

        for (int i = 1; i <= itemCount; i++) {
            System.out.println(messages.getString("item") + " " + i);

            System.out.print(messages.getString("enter.price"));
            double price = scanner.nextDouble();

            System.out.print(messages.getString("enter.quantity"));
            int quantity = scanner.nextInt();

            double itemTotal = calculator.calculateItemTotal(price, quantity);
            itemTotals.add(itemTotal);

            System.out.println(messages.getString("item.total") + " " + itemTotal);
        }

        double cartTotal = calculator.calculateCartTotal(itemTotals);
        System.out.println(messages.getString("cart.total") + " " + cartTotal);

        scanner.close();
    }
}