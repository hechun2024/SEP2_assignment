package com.hechun.shoppingcart;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartCalculatorTest {

    @Test
    void testCalculateItemTotal() {
        CartCalculator calculator = new CartCalculator();
        double result = calculator.calculateItemTotal(10.0, 3);
        assertEquals(30.0, result, 0.001);
    }

    @Test
    void testCalculateCartTotal() {
        CartCalculator calculator = new CartCalculator();
        double result = calculator.calculateCartTotal(Arrays.asList(20.0, 15.5, 4.5));
        assertEquals(40.0, result, 0.001);
    }

    @Test
    void testCalculateItemTotalWithZeroQuantity() {
        CartCalculator calculator = new CartCalculator();
        double result = calculator.calculateItemTotal(99.0, 0);
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void testEmptyCart() {
        CartCalculator calculator = new CartCalculator();
        assertEquals(0.0, calculator.calculateCartTotal(List.of()), 0.001);
    }

    @Test
    void testSelectEnglish() {
        Scanner scanner = new Scanner("1");
        Locale locale = LanguageSelector.selectLocale(scanner);
        assertEquals("en", locale.getLanguage());
    }

    @Test
    void testMainRuns() {
        App.main(new String[]{});
    }

    @Test
    void testInvalidLanguage() {
        assertThrows(Exception.class, () -> selector.select("invalid"));
    }
}