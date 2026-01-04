package ru.yandex.practicum.commerce.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AddProductRequest {
    @NotNull
    private String productId;
    @PositiveOrZero
    @NotNull
    private Integer quantity;
}
