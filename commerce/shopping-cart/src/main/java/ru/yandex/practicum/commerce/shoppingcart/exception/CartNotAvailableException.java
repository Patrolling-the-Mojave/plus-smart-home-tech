package ru.yandex.practicum.commerce.shoppingcart.exception;

public class CartNotAvailableException extends RuntimeException {
    public CartNotAvailableException(String message) {
        super(message);
    }
}
