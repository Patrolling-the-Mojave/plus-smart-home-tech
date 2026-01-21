package ru.yandex.practicum.commerce.payment.exception;

public class ProductCostIsNotCalculatedException extends RuntimeException {
    public ProductCostIsNotCalculatedException(String message) {
        super(message);
    }
}
