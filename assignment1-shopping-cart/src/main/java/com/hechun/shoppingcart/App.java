package com.hechun.shoppingcart;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // 1. 强制控制台使用 UTF-8 编码输出，避免在某些系统（如 Windows CMD）下显示乱码
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));

        // 2. 使用 UTF-8 编码创建 Scanner，确保用户输入的多语言字符能够正确读取
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

        scanner.close();  // 关闭 Scanner 以释放资源
    }
}