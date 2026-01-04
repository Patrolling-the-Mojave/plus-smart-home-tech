package ru.yandex.practicum.commerce.dto.warehouse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DimensionDto {
    private Double width;
    private Double height;
    private Double depth;

}
