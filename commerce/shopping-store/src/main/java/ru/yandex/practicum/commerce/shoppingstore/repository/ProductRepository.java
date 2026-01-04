package ru.yandex.practicum.commerce.shoppingstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);
}
