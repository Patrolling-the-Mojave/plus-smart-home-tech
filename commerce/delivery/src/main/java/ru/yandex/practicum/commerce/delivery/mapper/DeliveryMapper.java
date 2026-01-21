package ru.yandex.practicum.commerce.delivery.mapper;

import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryStatus;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;

import java.util.UUID;

public class DeliveryMapper {
    public static Delivery toEntity(DeliveryDto deliveryDto) {
        AddressDto from = deliveryDto.getFromAddress();
        AddressDto to = deliveryDto.getToAddress();
        return Delivery.builder()
                .id(UUID.randomUUID().toString())
                .originStreet(deliveryDto.getOrderId())
                .status(DeliveryStatus.IN_PROGRESS)
                .originCountry(from.getCountry())
                .originCity(from.getCity())
                .originStreet(from.getStreet())
                .originHouse(from.getHouse())
                .originFlat(from.getFlat())
                .destinationCountry(to.getCountry())
                .destinationCity(to.getCity())
                .destinationStreet(to.getStreet())
                .destinationHouse(to.getHouse())
                .destinationFlat(to.getFlat())
                .build();
    }

    public static DeliveryDto toDto(Delivery delivery){
        AddressDto from = AddressDto.builder()
                .country(delivery.getOriginCountry())
                .city(delivery.getOriginCity())
                .street(delivery.getOriginStreet())
                .house(delivery.getOriginHouse())
                .flat(delivery.getOriginFlat())
                .build();

        AddressDto to = AddressDto.builder()
                .country(delivery.getDestinationCountry())
                .city(delivery.getDestinationCity())
                .street(delivery.getDestinationStreet())
                .house(delivery.getDestinationHouse())
                .flat(delivery.getDestinationFlat())
                .build();

        return DeliveryDto.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .fromAddress(from)
                .toAddress(to)
                .deliveryCost(delivery.getDeliveryCost())
                .build();
    }
}
