package ru.yandex.practicum.commerce.order.repository;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.order.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByShoppingCartId(String shoppingCartId);
}
