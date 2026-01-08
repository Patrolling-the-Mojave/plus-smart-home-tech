package ru.yandex.practicum.commerce.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeProductQuantityRequest {
    @NotNull
    private String productId;
    @NotNull
    @PositiveOrZero
    private Integer newQuantity;
}
