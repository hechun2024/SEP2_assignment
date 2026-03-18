package com.hechun.shoppingcart;

import java.util.Locale;
import java.util.Scanner;

public class LanguageSelector {

    public static Locale selectLocale(Scanner scanner) {
        System.out.println("Select language / Valitse kieli / Välj språk / 言語を選択してください:");
        System.out.println("1. English");
        System.out.println("2. Finnish");
        System.out.println("3. Swedish");
        System.out.println("4. Japanese");
        System.out.print("Choice: ");

        int choice = scanner.nextInt();

        return switch (choice) {
            case 2 -> new Locale("fi", "FI");
            case 3 -> new Locale("sv", "SE");
            case 4 -> new Locale("ja", "JP");
            default -> new Locale("en", "US");
        };
    }
}