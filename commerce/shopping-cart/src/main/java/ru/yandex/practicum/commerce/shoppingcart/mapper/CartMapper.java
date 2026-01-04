package ru.yandex.practicum.commerce.shoppingcart.mapper;

import ru.yandex.practicum.commerce.dto.cart.CartProductDto;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.shoppingcart.model.ShoppingCart;

import java.util.List;

public class CartMapper {
    public static ShoppingCartDto toDto(ShoppingCart cart, List<CartProductDto> products){
        return ShoppingCartDto.builder()
                .products(products)
                .shoppingCartId(cart.getId())
                .build();
    }
}
