package ru.yandex.practicum.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.warehouse.model.ReservedProduct;

import java.util.List;

public interface ReservedProductRepository extends JpaRepository<ReservedProduct, String> {

    List<ReservedProduct> findAllByOrderId(String orderId);
}
