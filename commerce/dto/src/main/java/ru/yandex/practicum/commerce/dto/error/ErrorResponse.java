package ru.yandex.practicum.commerce.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String name;
    private String message;
}
