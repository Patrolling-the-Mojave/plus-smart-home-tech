package ru.yandex.practicum.commerce.payment.service;

import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;

public interface PaymentService {
    PaymentDto preparePayment(OrderDto orderDto);

    PaymentDto calculateProductCost(OrderDto orderDto);

    PaymentDto calculateTotalCost(OrderDto orderDto);

    PaymentDto setToSuccessfulPayment(OrderDto orderDto);

    PaymentDto setToUnsuccessfulPayment(OrderDto orderDto);
}
