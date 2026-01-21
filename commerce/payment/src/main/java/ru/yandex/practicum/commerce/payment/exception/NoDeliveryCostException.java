package ru.yandex.practicum.commerce.payment.exception;

public class NoDeliveryCostException extends RuntimeException {
    public NoDeliveryCostException(String message) {
        super(message);
    }
}
