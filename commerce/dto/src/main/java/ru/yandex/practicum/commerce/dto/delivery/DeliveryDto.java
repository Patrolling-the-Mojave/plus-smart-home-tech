package ru.yandex.practicum.commerce.dto.delivery;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;

import java.math.BigDecimal;

@Data
@Builder
public class DeliveryDto {
    private String deliveryId;
    private AddressDto fromAddress;
    private AddressDto toAddress;
    private String orderId;
    private DeliveryStatus status;
    private BigDecimal deliveryCost;
}
