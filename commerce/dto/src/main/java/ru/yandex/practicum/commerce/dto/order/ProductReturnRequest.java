package ru.yandex.practicum.commerce.dto.order;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ProductReturnRequest {
    private String orderId;
    private Map<String, Integer> products;
}
