package ru.yandex.practicum.commerce.payment.mapper;

import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.payment.model.Payment;

public class PaymentMapper {

    public static PaymentDto toDto(Payment payment){
        return PaymentDto.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .totalCost(payment.getTotalCost())
                .productCost(payment.getProductCost())
                .feeTotal(payment.getFeeTotal())
                .build();

    }
}
