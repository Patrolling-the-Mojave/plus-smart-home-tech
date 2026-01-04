package ru.yandex.practicum.commerce.shoppingstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductState;
import ru.yandex.practicum.commerce.dto.product.QuantityState;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    private String id;
    @Column(name = "product_name", nullable = false)
    private String productName;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "image_src", nullable = false)
    private String imageSrc;
    @Column(name = "quantity_state")
    @Enumerated(value = EnumType.STRING)
    private QuantityState quantityState;
    @Column(name = "product_category", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ProductCategory productCategory;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "product_state",nullable = false)
    private ProductState productState;
}
