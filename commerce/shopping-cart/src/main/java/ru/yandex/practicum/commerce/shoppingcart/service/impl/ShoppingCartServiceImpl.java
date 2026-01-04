package ru.yandex.practicum.commerce.shoppingcart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.cart.AddProductRequest;
import ru.yandex.practicum.commerce.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.interaction.client.WarehouseClient;
import ru.yandex.practicum.commerce.shoppingcart.exception.CartDeactivatedException;
import ru.yandex.practicum.commerce.shoppingcart.exception.CartNotAvailableException;
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
    public ShoppingCartDto addProductToCart(String username, AddProductRequest newProduct) {
        ShoppingCart cart = getCartById(username);
        checkCartStatus(cart);
        List<CartProduct> currentProducts = cartProductRepository.findAllByCartId(username);
        Map<String, CartProduct> productMap = currentProducts.stream().collect(Collectors.toMap(CartProduct::getProductId, Function.identity()));
        CartProduct cartProduct;
        if (productMap.containsKey(newProduct.getProductId())) {
            cartProduct = productMap.get(newProduct.getProductId());
            cartProduct.setQuantity(cartProduct.getQuantity() + newProduct.getQuantity());
        } else {
            cartProduct = CartProduct.builder()
                    .productId(newProduct.getProductId())
                    .quantity(newProduct.getQuantity())
                    .cartId(username)
                    .build();
            productMap.put(cartProduct.getProductId(), cartProduct);
        }
        List<CartProduct> updatedProducts = new ArrayList<>(productMap.values());
        ShoppingCartDto shoppingCart = toDto(cart, toDto(updatedProducts));
        BookedProductsDto bookedProduct = warehouseClient.checkShoppingCart(shoppingCart);
        if (bookedProduct == null){
            throw new CartNotAvailableException("ошибка при проверке наличия товаров на складе");
        }
        cartProductRepository.save(cartProduct);
        return shoppingCart;
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
        if (quantityDto.getNewQuantity() < 0){
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

    private void checkCartStatus(ShoppingCart cart){
        if (cart.getStatus().equals(CartStatus.DEACTIVATE)){
            throw new CartDeactivatedException("корзина пользователя " + cart.getId() + " была деактивирована");
        }
    }
}
