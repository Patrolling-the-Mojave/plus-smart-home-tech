package ru.yandex.practicum.commerce.shoppingcart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.cart.AddProductRequest;
import ru.yandex.practicum.commerce.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.client.ShoppingCartClient;
import ru.yandex.practicum.commerce.shoppingcart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartClient {
    private final ShoppingCartService shoppingCartService;


    @GetMapping
    @Override
    public ShoppingCartDto findShoppingCartById(@RequestParam String username) {
        return shoppingCartService.findCartById(username);
    }

    @PutMapping
    @Override
    public ShoppingCartDto addProductToCart(@RequestParam String username, @RequestBody Map<String, Integer> products) {
        return shoppingCartService.addProductToCart(username, products);
    }

    @DeleteMapping
    @Override
    public void deactivateShoppingCart(@RequestParam String username) {
        shoppingCartService.deactivateCart(username);
    }

    @PostMapping
    @Override
    public ShoppingCartDto removeProductsFormCart(@RequestParam String username, @RequestBody List<String> productIds) {
        return shoppingCartService.removeProductsFromCart(username, productIds);
    }

    @PostMapping("/change-quantity")
    @Override
    public ShoppingCartDto changeQuantity(@RequestParam String username, @Validated @RequestBody ChangeProductQuantityRequest changeQuantityRequest) {
        return shoppingCartService.setProductQuantity(username, changeQuantityRequest);
    }


}
