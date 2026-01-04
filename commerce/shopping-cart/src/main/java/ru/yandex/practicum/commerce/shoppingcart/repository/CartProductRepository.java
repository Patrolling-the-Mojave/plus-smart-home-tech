package ru.yandex.practicum.commerce.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.shoppingcart.model.CartProduct;
import ru.yandex.practicum.commerce.shoppingcart.model.CartProductId;

import java.util.List;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, CartProductId> {
    List<CartProduct> findAllByCartId(String id);

    Optional<CartProduct> findByCartIdAndProductId(String cartId, String productId);

    void deleteAllByCartIdAndProductIdIn(String cartId, List<String> productId);
}
