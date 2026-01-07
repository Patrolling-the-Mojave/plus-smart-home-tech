package ru.yandex.practicum.commerce.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ShoppingCartDto {
    private String shoppingCartId;
    private Map<String, Integer> products;
}
