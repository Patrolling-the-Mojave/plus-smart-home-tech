package ru.yandex.practicum.commerce.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;

@FeignClient(name = "payment")
public interface PaymentClient {

    @PostMapping("/api/v1/payment")
    PaymentDto preparePayment(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/totalCost")
    PaymentDto calculateTotalCost(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/productCost")
    PaymentDto calculateProductCost(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/refund")
    PaymentDto refund(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/failed")
    PaymentDto failed(@RequestBody OrderDto orderDto);
}

