package ru.yandex.practicum.commerce.order.service;

import org.aspectj.weaver.ast.Or;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.order.model.Order;

import java.util.List;

public interface OrderService {
    List<OrderDto> findOrdersByUserid(String userId);

    OrderDto createNewOrder(CreateNewOrderRequest newOrderRequest);

    OrderDto returnProducts(ProductReturnRequest returnRequest);

    OrderDto payForTheOrder(String orderId);

    OrderDto failPayment(String orderId);

    OrderDto addOrderToDelivery(String orderId);

    OrderDto failDelivery(String orderId);

    OrderDto completeOrder(String orderId);

    OrderDto calculateTotalCost(String orderId);

    OrderDto calculateProductCost(String orderId);

    OrderDto calculateDeliveryCost(String orderId);
}
