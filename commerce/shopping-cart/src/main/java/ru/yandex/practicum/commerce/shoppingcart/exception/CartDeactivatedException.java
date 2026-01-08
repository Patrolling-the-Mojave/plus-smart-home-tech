package ru.yandex.practicum.commerce.shoppingcart.exception;

public class CartDeactivatedException extends RuntimeException {
    public CartDeactivatedException(String message) {
        super(message);
    }
}
