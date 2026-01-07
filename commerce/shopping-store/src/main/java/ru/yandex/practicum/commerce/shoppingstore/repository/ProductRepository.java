package ru.yandex.practicum.commerce.shoppingstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findAllByProductCategory(@Param("product_category") ProductCategory productCategory, Pageable pageable);
}
