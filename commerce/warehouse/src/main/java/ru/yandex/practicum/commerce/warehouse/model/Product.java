package ru.yandex.practicum.commerce.warehouse.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "product_id", nullable = false)
    private String productId;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "fragile", nullable = false)
    private Boolean fragile;
    @Column(name = "width", nullable = false)
    private Double width;
    @Column(name = "height", nullable = false)
    private Double height;
    @Column(name = "depth", nullable = false)
    private Double depth;
    @Column(name = "weight", nullable = false)
    private Double weight;
}
