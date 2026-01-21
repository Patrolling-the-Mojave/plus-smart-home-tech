package ru.yandex.practicum.commerce.order.mapper;

import org.aspectj.weaver.ast.Or;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.order.model.Order;

import java.util.List;
import java.util.Map;

public class OrderMapper {

    public static OrderDto toDto(Order order, Map<String, Integer> products){
        return OrderDto.builder()
                .orderId(order.getId().toString())
                .state(order.getState())
                .shoppingCartId(order.getShoppingCartId())
                .deliveryId(order.getDeliveryId())
                .paymentId(order.getPaymentId())
                .deliveryPrice(order.getDeliveryPrice())
                .deliveryVolume(order.getDeliveryVolume())
                .deliveryWeight(order.getDeliveryWeight())
                .fragile(order.getFragile())
                .productPrice(order.getProductPrice())
                .totalPrice(order.getTotalPrice())
                .products(products)
                .build();
    }

}
