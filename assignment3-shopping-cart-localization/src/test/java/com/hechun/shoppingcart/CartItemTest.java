package com.hechun.shoppingcart;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartItemTest {

    @Test
    void gettersReflectConstructorParameters() {
        CartItem item = new CartItem(5, 4.5, 3, 13.5);

        assertEquals(5, item.getItemNumber());
        assertEquals(4.5, item.getPrice());
        assertEquals(3, item.getQuantity());
        assertEquals(13.5, item.getSubtotal());
    }
}
