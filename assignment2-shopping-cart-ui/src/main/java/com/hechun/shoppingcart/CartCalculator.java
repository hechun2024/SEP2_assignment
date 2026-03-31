package com.hechun.shoppingcart;

import java.util.List;

public class CartCalculator {

    public static double calculateItemTotal(double price, int quantity) {
        return price * quantity;
    }

    public static double calculateCartTotal(List<Double> itemTotals) {
        return itemTotals.stream().mapToDouble(Double::doubleValue).sum();
    }
}