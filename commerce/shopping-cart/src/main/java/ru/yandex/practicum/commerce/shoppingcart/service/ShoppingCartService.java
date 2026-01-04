package ru.yandex.practicum.commerce.shoppingcart.service;

import ru.yandex.practicum.commerce.dto.cart.AddProductRequest;
import ru.yandex.practicum.commerce.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;

import java.util.List;

public interface ShoppingCartService {
    ShoppingCartDto findCartById(String username);

    ShoppingCartDto addProductToCart(String username, AddProductRequest newProduct);

    void deactivateCart(String username);

    ShoppingCartDto removeProductsFromCart(String username, List<String> productIds);

    ShoppingCartDto setProductQuantity(String username, ChangeProductQuantityRequest quantityDto);
}
