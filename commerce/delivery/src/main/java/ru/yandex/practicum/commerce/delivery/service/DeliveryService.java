package ru.yandex.practicum.commerce.delivery.service;

import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

public interface DeliveryService {

    DeliveryDto prepareDelivery(DeliveryDto deliveryDto);

    DeliveryDto calculateDeliveryCost(OrderDto orderDto);

    DeliveryDto setToSuccessfulState(String deliveryId);

    DeliveryDto setToUnsuccessfulState(String deliveryId);

    DeliveryDto pickDelivery(String deliveryId);

}
