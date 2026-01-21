package ru.yandex.practicum.commerce.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.order.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> findOrdersByUsername(@RequestParam String username) {
        return orderService.findOrdersByUserid(username);
    }

    @PutMapping
    public OrderDto createNewOrder(@RequestBody CreateNewOrderRequest newOrderRequest) {
        return orderService.createNewOrder(newOrderRequest);
    }

    @PostMapping("/return")
    public OrderDto returnProducts(@RequestBody ProductReturnRequest returnRequest) {
        return orderService.returnProducts(returnRequest);
    }

    @PostMapping("/payment")
    public OrderDto payForTheOrder(@RequestBody String orderId) {
        return orderService.payForTheOrder(orderId.replaceAll("^\"|\"$", ""));
    }

    @PostMapping("/payment/failed")
    public OrderDto failPayment(@RequestBody String orderId) {
        return orderService.failPayment(orderId.replaceAll("^\"|\"$", ""));
    }

    @PostMapping("/delivery")
    public OrderDto addOrderToDelivery(@RequestBody String orderId) {
        return orderService.addOrderToDelivery(orderId.replaceAll("^\"|\"$", ""));
    }

    @PostMapping("/delivery/failed")
    public OrderDto failDelivery(@RequestBody String orderId) {
        return orderService.failDelivery(orderId.replaceAll("^\"|\"$", ""));
    }

    @PostMapping("/completed")
    public OrderDto completeOrder(@RequestBody String orderId) {
        return orderService.completeOrder(orderId.replaceAll("^\"|\"$", ""));
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalCost(@RequestBody String orderId) {
        return orderService.calculateTotalCost(orderId.replaceAll("^\"|\"$", ""));
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(@RequestBody String orderId) {
        return orderService.calculateDeliveryCost(orderId.replaceAll("^\"|\"$", ""));
    }

    @PostMapping("/calculate/product")
    public OrderDto calculateProductCost(@RequestBody String orderId) {
        return orderService.calculateProductCost(orderId.replaceAll("^\"|\"$", ""));
    }
}
