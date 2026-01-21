package ru.yandex.practicum.commerce.dto.payment;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentDto {
    private String paymentId;
    private String orderId;
    private BigDecimal totalCost;
    private BigDecimal productCost;
    private BigDecimal feeTotal;

}
