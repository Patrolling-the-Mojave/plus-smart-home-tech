package ru.yandex.practicum.commerce.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interaction.client.PaymentClient;
import ru.yandex.practicum.commerce.payment.service.PaymentService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentClient {
    private final PaymentService paymentService;

    @PostMapping
    @Override
    public PaymentDto preparePayment(@RequestBody OrderDto orderDto) {
        return paymentService.preparePayment(orderDto);
    }

    @PostMapping("/totalCost")
    @Override
    public PaymentDto calculateTotalCost(@RequestBody OrderDto orderDto) {
        return paymentService.calculateTotalCost(orderDto);
    }

    @PostMapping("/productCost")
    @Override
    public PaymentDto calculateProductCost(@RequestBody OrderDto orderDto) {
        return paymentService.calculateProductCost(orderDto);
    }

    @PostMapping("/refund")
    @Override
    public PaymentDto refund(@RequestBody OrderDto orderDto) {
        return paymentService.setToSuccessfulPayment(orderDto);
    }

    @PostMapping("/failed")
    @Override
    public PaymentDto failed(@RequestBody OrderDto orderDto) {
        return paymentService.setToUnsuccessfulPayment(orderDto);
    }

}
