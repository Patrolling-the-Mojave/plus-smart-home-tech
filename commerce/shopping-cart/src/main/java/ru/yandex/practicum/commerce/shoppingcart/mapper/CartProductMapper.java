package ru.yandex.practicum.commerce.shoppingcart.mapper;

import ru.yandex.practicum.commerce.dto.cart.CartProductDto;
import ru.yandex.practicum.commerce.shoppingcart.model.CartProduct;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartProductMapper {

    public static Map<String, Integer> toDto(List<CartProduct> products) {
        return products.stream().collect(Collectors.toMap(CartProduct::getProductId, CartProduct::getQuantity));
    }

}
