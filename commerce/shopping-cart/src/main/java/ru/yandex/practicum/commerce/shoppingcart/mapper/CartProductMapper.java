package ru.yandex.practicum.commerce.shoppingcart.mapper;

import ru.yandex.practicum.commerce.dto.cart.CartProductDto;
import ru.yandex.practicum.commerce.shoppingcart.model.CartProduct;

import java.util.List;

public class CartProductMapper {

    public static CartProductDto toDto(CartProduct cartProduct) {
        return CartProductDto.builder()
                .productId(cartProduct.getProductId())
                .quantity(cartProduct.getQuantity())
                .build();
    }

    public static List<CartProductDto> toDto(List<CartProduct> products) {
        return products.stream().map(CartProductMapper::toDto).toList();
    }

}
