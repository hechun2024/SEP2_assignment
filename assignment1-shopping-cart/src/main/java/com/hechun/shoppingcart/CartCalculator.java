package com.hechun.shoppingcart;

import java.util.List;

public class CartCalculator {

    public double calculateItemTotal(double price, int quantity) {
        return price * quantity;
    }

    public double calculateCartTotal(List<Double> itemTotals) {
        return itemTotals.stream().mapToDouble(Double::doubleValue).sum();
    }
}