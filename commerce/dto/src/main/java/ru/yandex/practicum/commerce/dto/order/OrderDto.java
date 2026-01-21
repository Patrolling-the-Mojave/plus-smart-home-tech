package ru.yandex.practicum.commerce.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class OrderDto {
    private String orderId;
    private String shoppingCartId;
    private Map<String, Integer> products;
    private String paymentId;
    private String deliveryId;
    private OrderState state;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal productPrice;

}
