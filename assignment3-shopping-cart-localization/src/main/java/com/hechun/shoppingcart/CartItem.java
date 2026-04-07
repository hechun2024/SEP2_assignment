package com.hechun.shoppingcart;

public class CartItem {
    private int itemNumber;
    private double price;
    private int quantity;
    private double subtotal;

    public CartItem(int itemNumber, double price, int quantity, double subtotal) {
        this.itemNumber = itemNumber;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }
}