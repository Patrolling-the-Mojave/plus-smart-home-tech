package ru.yandex.practicum.commerce.order.exception;

public class PaymentClientException extends RuntimeException {
    public PaymentClientException(String message) {
        super(message);
    }
}
