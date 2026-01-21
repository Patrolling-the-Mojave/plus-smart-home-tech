package ru.yandex.practicum.commerce.delivery.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.delivery.exception.NotFoundException;
import ru.yandex.practicum.commerce.delivery.exception.WarehouseClientException;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryStatus;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.interaction.client.WarehouseClient;

import java.math.BigDecimal;

import static ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper.toDto;
import static ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper.toEntity;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final WarehouseClient warehouseClient;

    private static final Double BASED_DELIVERY_COST = 5.00;

    @Override
    @Transactional
    public DeliveryDto calculateDeliveryCost(OrderDto orderDto) {
        Delivery delivery = getDeliveryById(orderDto.getDeliveryId());
        BookedProductsDto bookedProducts = warehouseClient.collectOrderProducts(orderDto);
        if (bookedProducts == null) {
            throw new WarehouseClientException("произошла ошибка при проверке наличия товаров на складе");
        }
        delivery.setFragile(bookedProducts.getFragile());
        delivery.setTotalVolume(bookedProducts.getDeliveryVolume());
        delivery.setTotalWeight(bookedProducts.getDeliveryWeight());
        Double deliveryCost = BASED_DELIVERY_COST;
        if (delivery.getOriginStreet().equals("ADDRESS_2")) {
            deliveryCost += deliveryCost * 2;
        }
        if (delivery.getFragile()) {
            deliveryCost += deliveryCost * 0.2;
        }
        deliveryCost += delivery.getTotalWeight() * 0.3;
        deliveryCost += delivery.getTotalVolume() * 0.2;
        if (!delivery.getOriginStreet().equals(delivery.getDestinationStreet())) {
            deliveryCost += deliveryCost * 0.2;
        }
        delivery.setDeliveryCost(BigDecimal.valueOf(deliveryCost));
        deliveryRepository.save(delivery);
        return toDto(delivery);
    }

    @Override
    @Transactional
    public DeliveryDto prepareDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = toEntity(deliveryDto);
        delivery.setStatus(DeliveryStatus.CREATED);
        return toDto(deliveryRepository.save(delivery));
    }

    @Override
    public DeliveryDto setToSuccessfulState(String orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setStatus(DeliveryStatus.DELIVERED);
        deliveryRepository.save(delivery);
        return toDto(delivery);
    }

    @Override
    public DeliveryDto setToUnsuccessfulState(String orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setStatus(DeliveryStatus.CANCELLED);
        deliveryRepository.save(delivery);
        return toDto(delivery);
    }

    @Override
    public DeliveryDto pickDelivery(String orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        warehouseClient.addDeliveryToAssembledOrder(delivery.getOrderId(), delivery.getId());
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        deliveryRepository.save(delivery);
        return toDto(delivery);
    }

    private Delivery getDeliveryById(String deliveryId) {
        return deliveryRepository.findById(deliveryId).orElseThrow(() ->
                new NotFoundException("доставка с id " + deliveryId + " не найдена"));
    }

    private Delivery getDeliveryByOrderId(String orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId);
        if (delivery == null) {
            throw new NotFoundException("доставка с orderId " + orderId + " не найдена");
        }
        return delivery;
    }
}
