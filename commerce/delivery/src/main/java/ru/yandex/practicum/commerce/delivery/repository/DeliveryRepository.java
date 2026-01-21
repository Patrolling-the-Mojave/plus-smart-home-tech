package ru.yandex.practicum.commerce.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.delivery.model.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {
    Delivery findByOrderId(String orderId);
}
