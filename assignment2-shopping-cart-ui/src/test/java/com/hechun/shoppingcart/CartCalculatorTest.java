package com.hechun.shoppingcart;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartCalculatorTest {

    @Test
    void testCalculateItemTotal() {
        double result = CartCalculator.calculateItemTotal(10.0, 3);
        assertEquals(30.0, result, 0.001);
    }

    @Test
    void testCalculateItemTotal_zeroQuantity() {
        double result = CartCalculator.calculateItemTotal(99.0, 0);
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void testCalculateItemTotal_zeroPrice() {
        double result = CartCalculator.calculateItemTotal(0.0, 5);
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void testCalculateCartTotal() {
        List<Double> items = Arrays.asList(20.0, 15.5, 4.5);
        double result = CartCalculator.calculateCartTotal(items);
        assertEquals(40.0, result, 0.001);
    }

    @Test
    void testCalculateCartTotal_singleItem() {
        List<Double> items = List.of(12.5);
        double result = CartCalculator.calculateCartTotal(items);
        assertEquals(12.5, result, 0.001);
    }

    @Test
    void testCalculateCartTotal_emptyList() {
        List<Double> items = List.of();
        double result = CartCalculator.calculateCartTotal(items);
        assertEquals(0.0, result, 0.001);
    }
}