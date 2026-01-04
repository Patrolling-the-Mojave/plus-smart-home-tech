package ru.yandex.practicum.commerce.shoppingcart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CartProductId.class)
@Table(name = "cart_product")
public class CartProduct {
    @Id
    @Column(name = "cart_id", nullable = false)
    private String cartId;
    @Id
    @Column(name = "product_id", nullable = false)
    private String productId;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
