package com.hechun.shoppingcart;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageSelectorTest {

    @Test
    void selectsEnglishByDefaultForUnknownChoice() {
        try (Scanner scanner = new Scanner("5")) {
            Locale locale = LanguageSelector.selectLocale(scanner);
            assertEquals(Locale.forLanguageTag("en-US"), locale);
        }
    }

    @Test
    void selectsFinnishWhenChoosingTwo() {
        try (Scanner scanner = new Scanner("2")) {
            Locale locale = LanguageSelector.selectLocale(scanner);
            assertEquals(Locale.forLanguageTag("fi-FI"), locale);
        }
    }

    @Test
    void selectsSwedishWhenChoosingThree() {
        try (Scanner scanner = new Scanner("3")) {
            Locale locale = LanguageSelector.selectLocale(scanner);
            assertEquals(Locale.forLanguageTag("sv-SE"), locale);
        }
    }

    @Test
    void selectsJapaneseWhenChoosingFour() {
        try (Scanner scanner = new Scanner("4")) {
            Locale locale = LanguageSelector.selectLocale(scanner);
            assertEquals(Locale.forLanguageTag("ja-JP"), locale);
        }
    }
}
