package ru.yandex.practicum.commerce.shoppingcart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.interaction.client.WarehouseClient;
import ru.yandex.practicum.commerce.shoppingcart.exception.CartDeactivatedException;
import ru.yandex.practicum.commerce.shoppingcart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.shoppingcart.exception.NotFoundException;
import ru.yandex.practicum.commerce.shoppingcart.model.CartProduct;
import ru.yandex.practicum.commerce.shoppingcart.model.CartStatus;
import ru.yandex.practicum.commerce.shoppingcart.model.ShoppingCart;
import ru.yandex.practicum.commerce.shoppingcart.repository.CartProductRepository;
import ru.yandex.practicum.commerce.shoppingcart.repository.ShoppingCartRepository;
import ru.yandex.practicum.commerce.shoppingcart.service.ShoppingCartService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.yandex.practicum.commerce.shoppingcart.mapper.CartMapper.toDto;
import static ru.yandex.practicum.commerce.shoppingcart.mapper.CartProductMapper.toDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartProductRepository cartProductRepository;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto findCartById(String username) {
        ShoppingCart cart = getCartById(username);
        List<CartProduct> products = cartProductRepository.findAllByCartId(username);
        return toDto(cart, toDto(products));
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToCart(String username, Map<String, Integer> products) {
        if (products.isEmpty()) {
            throw new IllegalArgumentException("список товаров в корзине пуст");
        }
        ShoppingCart cart;
        List<CartProduct> cartProducts;
        if (shoppingCartRepository.existsById(username)) {
            cart = getCartById(username);
            checkCartStatus(cart);
            cartProducts = addToExistingCart(username, products);
        } else {
            cart = ShoppingCart.builder()
                    .id(username)
                    .status(CartStatus.ACTIVE)
                    .build();
            cartProducts = createNewShoppingCart(cart, products);
            shoppingCartRepository.save(cart);
        }
        ShoppingCartDto shoppingCart = toDto(cart, toDto(cartProducts));
        BookedProductsDto bookedProduct = warehouseClient.checkShoppingCart(shoppingCart);
        if (bookedProduct == null) {
            throw new NoProductsInShoppingCartException("ошибка при проверке наличия товаров на складе");
        }
        cartProductRepository.saveAll(cartProducts);
        return shoppingCart;
    }

    private List<CartProduct> createNewShoppingCart(ShoppingCart shoppingCart, Map<String, Integer> products) {
        List<CartProduct> cartProducts = new ArrayList<>();
        for (String productId : products.keySet()) {
            CartProduct cartProduct = CartProduct.builder()
                    .cartId(shoppingCart.getId())
                    .productId(productId)
                    .quantity(products.get(productId))
                    .build();
            cartProducts.add(cartProduct);
        }
        return cartProducts;
    }

    private List<CartProduct> addToExistingCart(String username, Map<String, Integer> products) {
        List<CartProduct> currentProducts = cartProductRepository.findAllByCartId(username);
        Map<String, CartProduct> productMap = currentProducts.stream().collect(Collectors.toMap(CartProduct::getProductId, Function.identity()));
        for (String productId : products.keySet()) {
            CartProduct cartProduct;
            if (productMap.containsKey(productId)) {
                cartProduct = productMap.get(productId);
                cartProduct.setQuantity(cartProduct.getQuantity() + products.get(productId));
            } else {
                cartProduct = CartProduct.builder()
                        .productId(productId)
                        .quantity(products.get(productId))
                        .cartId(username)
                        .build();
                productMap.put(cartProduct.getProductId(), cartProduct);
            }
        }
        return new ArrayList<>(productMap.values());
    }

    @Override
    @Transactional
    public void deactivateCart(String username) {
        ShoppingCart cart = getCartById(username);
        cart.setStatus(CartStatus.DEACTIVATE);
        shoppingCartRepository.save(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username, List<String> productIds) {
        ShoppingCart cart = getCartById(username);
        checkCartStatus(cart);
        cartProductRepository.deleteAllByCartIdAndProductIdIn(username, productIds);
        List<CartProduct> products = cartProductRepository.findAllByCartId(username);
        return toDto(cart, toDto(products));
    }

    @Override
    @Transactional
    public ShoppingCartDto setProductQuantity(String username, ChangeProductQuantityRequest quantityDto) {
        ShoppingCart cart = getCartById(username);
        checkCartStatus(cart);
        if (quantityDto.getNewQuantity() < 0) {
            throw new IllegalArgumentException("количество товара должно быть больше 0");
        }
        Optional<CartProduct> productOpt = cartProductRepository.findByCartIdAndProductId(username, quantityDto.getProductId());
        if (productOpt.isEmpty()) {
            throw new NotFoundException("товар " + quantityDto.getProductId() + " не найден в корзине пользователя" + username);
        }
        CartProduct product = productOpt.get();
        product.setQuantity(quantityDto.getNewQuantity());
        cartProductRepository.save(product);
        List<CartProduct> products = cartProductRepository.findAllByCartId(username);
        return toDto(cart, toDto(products));
    }

    private ShoppingCart getCartById(String cartId) {
        return shoppingCartRepository.findById(cartId).orElseThrow(() ->
                new NotFoundException("корзина покупок с id" + cartId + " не найдена"));
    }

    private void checkCartStatus(ShoppingCart cart) {
        if (cart.getStatus().equals(CartStatus.DEACTIVATE)) {
            throw new CartDeactivatedException("корзина пользователя " + cart.getId() + " была деактивирована");
        }
    }
}
