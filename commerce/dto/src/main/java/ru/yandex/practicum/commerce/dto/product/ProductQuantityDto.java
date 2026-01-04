package ru.yandex.practicum.commerce.dto.product;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductQuantityDto {
    private String id;
    private QuantityState quantityState;
}
