package ru.yandex.practicum.commerce.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.client.DeliveryClient;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryClient {
    private final DeliveryService deliveryService;

    @Override
    @PostMapping("/picked")
    public void addOrderToDelivery(String orderId) {
        deliveryService.pickDelivery(orderId.replaceAll("^\"|\"$", ""));
    }

    @Override
    @PostMapping("/cost")
    public DeliveryDto calculateDeliveryCost(OrderDto orderDto) {
        return deliveryService.calculateDeliveryCost(orderDto);
    }

    @Override
    @PutMapping
    public DeliveryDto prepareDelivery(DeliveryDto deliveryDto) {
        return deliveryService.prepareDelivery(deliveryDto);
    }

    @Override
    @PostMapping("/successful")
    public void setToSuccessfulStatus(String orderId) {
        deliveryService.setToSuccessfulState(orderId.replaceAll("^\"|\"$", ""));
    }

    @Override
    @PostMapping("/failed")
    public void setToUnsuccessfulStatus(String orderId) {
        deliveryService.setToUnsuccessfulState(orderId.replaceAll("^\"|\"$", ""));
    }
}
