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
    private String productId;
    @NotNull
    private String productName;
    @NotNull
    private String description;
    @NotNull
    private String imageSrc;
    @NotNull
    private QuantityState quantityState;
    @NotNull
    private ProductState productState;
    @NotNull
    private ProductCategory productCategory;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;
}
