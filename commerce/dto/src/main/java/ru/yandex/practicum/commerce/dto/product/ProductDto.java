package ru.yandex.practicum.commerce.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    @NotNull
    private String id;
    @NotNull
    private String productName;
    @NotNull
    private String description;
    @NotNull
    private String imageSrc;
    private QuantityState quantityState;
    private ProductState productState;
    private ProductCategory productCategory;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;
}
