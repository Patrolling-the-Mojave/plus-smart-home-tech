package ru.yandex.practicum.commerce.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewProductInWarehouseRequest {
    @NotNull
    private String productId;
    @NotNull
    private Boolean fragile;
    @NotNull
    private DimensionDto dimension;
    @NotNull
    @PositiveOrZero
    private Double weight;
}
