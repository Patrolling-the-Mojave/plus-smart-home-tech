package ru.yandex.practicum.commerce.dto.cart;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartProductDto {
    private String productId;
    private Integer quantity;
}
