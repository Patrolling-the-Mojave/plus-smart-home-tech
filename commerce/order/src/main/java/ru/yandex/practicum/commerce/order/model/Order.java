package ru.yandex.practicum.commerce.order.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.commerce.dto.order.OrderState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "orders")
public class Order {
    @Id
    private UUID id;
    @Column(name = "shopping_cart_id", nullable = false, unique = true)
    private String shoppingCartId;
    @Column(name = "payment_id", nullable = false, unique = true)
    private String paymentId;
    @Column(name = "delivery_id", nullable = false, unique = true)
    private String deliveryId;
    @Enumerated(value = EnumType.STRING)
    private OrderState state;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal productPrice;

}
