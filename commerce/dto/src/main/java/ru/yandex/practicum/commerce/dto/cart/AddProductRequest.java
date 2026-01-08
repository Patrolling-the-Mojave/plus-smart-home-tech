package ru.yandex.practicum.commerce.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AddProductRequest {
    @NotNull
    private String productId;
    private Integer quantity;
}
