package com.hechun.shoppingcart;

public class CartServiceException extends RuntimeException {
    public CartServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
