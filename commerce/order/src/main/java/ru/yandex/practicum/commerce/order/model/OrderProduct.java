package ru.yandex.practicum.commerce.order.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@IdClass(OrderProductId.class)
@Builder
@Table(name = "order_products")
public class OrderProduct {
    @Id
    private String productId;
    @Id
    private String orderId;
    private Integer quantity;
}
