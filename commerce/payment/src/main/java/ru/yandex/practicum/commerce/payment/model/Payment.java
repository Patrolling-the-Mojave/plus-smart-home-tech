package ru.yandex.practicum.commerce.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.commerce.dto.payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Payment {
    @Id
    private String id;
    @Column(name = "order_id", nullable = false)
    private String orderId;
    private BigDecimal productCost;
    private BigDecimal totalCost;
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;
    private BigDecimal feeTotal;
}
