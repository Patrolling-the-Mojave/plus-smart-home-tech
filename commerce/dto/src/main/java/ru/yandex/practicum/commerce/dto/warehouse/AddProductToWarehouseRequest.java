package ru.yandex.practicum.commerce.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddProductToWarehouseRequest {
    @NotNull
    private String productId;
    @NotNull
    @Min(1)
    private Integer quantity;
}
