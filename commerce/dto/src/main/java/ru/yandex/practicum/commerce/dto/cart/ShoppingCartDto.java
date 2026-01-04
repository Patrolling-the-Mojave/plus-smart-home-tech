package ru.yandex.practicum.commerce.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ShoppingCartDto {
    private String shoppingCartId;
    private List<CartProductDto> products;
}
