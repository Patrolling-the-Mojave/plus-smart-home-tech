package ru.yandex.practicum.commerce.order.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderState;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.interaction.client.DeliveryClient;
import ru.yandex.practicum.commerce.interaction.client.PaymentClient;
import ru.yandex.practicum.commerce.interaction.client.WarehouseClient;
import ru.yandex.practicum.commerce.order.exception.DeliveryClientException;
import ru.yandex.practicum.commerce.order.exception.NotFoundException;
import ru.yandex.practicum.commerce.order.exception.PaymentClientException;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.model.OrderProduct;
import ru.yandex.practicum.commerce.order.repository.OrderProductRepository;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;
import ru.yandex.practicum.commerce.order.service.OrderService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.commerce.order.mapper.OrderMapper.toDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final WarehouseClient warehouseClient;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    @Override
    public OrderDto addOrderToDelivery(String orderId) {
        Order order = getOrderById(orderId);
        deliveryClient.addOrderToDelivery(orderId);
        order.setState(OrderState.ON_DELIVERY);
        orderRepository.save(order);
        Map<String, Integer> orderProductMap = orderProductRepository.findAllByOrderId(orderId).stream().collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));
        return toDto(order, orderProductMap);
    }

    @Override
    public List<OrderDto> findOrdersByUserid(String userId) {
        List<Order> orders = orderRepository.findAllByShoppingCartId(userId);
        List<String> orderIds = orders.stream().map(Order::getId).map(UUID::toString).toList();
        List<OrderProduct> products = orderProductRepository.findAllByOrderIdIn(orderIds);
        Map<String, List<OrderProduct>> productsByOrderId = products.stream().collect(Collectors.groupingBy(OrderProduct::getOrderId));
        return orders.stream().map(order -> {
            String orderId = order.getId().toString();
            Map<String, Integer> productMap = productsByOrderId.getOrDefault(orderId, Collections.emptyList())
                    .stream()
                    .collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));
            return toDto(order, productMap);
        }).toList();
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest newOrderRequest) {
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .shoppingCartId(newOrderRequest.getShoppingCart().getShoppingCartId())
                .state(OrderState.NEW)
                .build();
        List<OrderProduct> orderedProducts = new ArrayList<>();
        for (Map.Entry<String, Integer> product : newOrderRequest.getShoppingCart().getProducts().entrySet()) {
            OrderProduct orderedProduct = OrderProduct.builder()
                    .productId(product.getKey())
                    .orderId(order.getId().toString())
                    .quantity(product.getValue())
                    .build();
            orderedProducts.add(orderedProduct);
        }
        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
        AddressDto destinationAddress = AddressDto.builder()
                .country(newOrderRequest.getDeliveryAddress().getCountry())
                .city(newOrderRequest.getDeliveryAddress().getCity())
                .street(newOrderRequest.getDeliveryAddress().getStreet())
                .house(newOrderRequest.getDeliveryAddress().getHouse())
                .flat(newOrderRequest.getDeliveryAddress().getFlat())
                .build();
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .toAddress(destinationAddress)
                .fromAddress(warehouseAddress)
                .orderId(order.getId().toString())
                .build();

        DeliveryDto preparedDelivery = deliveryClient.prepareDelivery(deliveryDto);
        order.setDeliveryId(preparedDelivery.getDeliveryId());
        PaymentDto paymentDto = paymentClient.preparePayment(toDto(order, newOrderRequest.getShoppingCart().getProducts()));
        order.setPaymentId(paymentDto.getPaymentId());
        orderRepository.save(order);
        orderProductRepository.saveAll(orderedProducts);
        return toDto(order, newOrderRequest.getShoppingCart().getProducts());
    }

    @Override
    public OrderDto returnProducts(ProductReturnRequest returnRequest) {
        Order order = getOrderById(returnRequest.getOrderId());
        Map<String, Integer> orderProductMap = orderProductRepository.findAllByOrderId(order.getId().toString()).stream().collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));
        warehouseClient.returnProducts(returnRequest);
        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);
        return toDto(order, orderProductMap);
    }

    @Override
    public OrderDto payForTheOrder(String orderId) {
        Order order = getOrderById(orderId);
        Map<String, Integer> orderProductMap = orderProductRepository.findAllByOrderId(order.getId().toString()).stream().collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));
        OrderDto orderDto = toDto(order, orderProductMap);
        paymentClient.refund(orderDto);
        order.setState(OrderState.ON_PAYMENT);
        orderRepository.save(order);
        return toDto(order, orderProductMap);
    }

    @Override
    public OrderDto failPayment(String orderId) {
        Order order = getOrderById(orderId);
        Map<String, Integer> orderProductMap = orderProductRepository.findAllByOrderId(orderId).stream().collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));
        OrderDto orderDto = toDto(order, orderProductMap);
        PaymentDto paymentDto = paymentClient.failed(orderDto);
        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);
        return toDto(order, orderProductMap);
    }

    @Override
    public OrderDto failDelivery(String orderId) {
        Order order = getOrderById(orderId);
        Map<String, Integer> orderProductMap = orderProductRepository.findAllByOrderId(orderId).stream().collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));
        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);
        OrderDto orderDto = toDto(order, orderProductMap);
        deliveryClient.setToUnsuccessfulStatus(orderId);
        return orderDto;
    }

    @Override
    public OrderDto completeOrder(String orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.COMPLETED);
        orderRepository.save(order);
        Map<String, Integer> orderProductMap = orderProductRepository.findAllByOrderId(orderId).stream().collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));
        return toDto(order, orderProductMap);
    }

    @Override
    public OrderDto calculateTotalCost(String orderId) {
        Order order = getOrderById(orderId);
        List<OrderProduct> products = orderProductRepository.findAllByOrderId(orderId);
        if (products == null || products.isEmpty()) {
            throw new NotFoundException("для заказа " + orderId + " не найдено ни одного товара");
        }
        Map<String, Integer> productMap = products.stream().collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));
        if (order.getDeliveryPrice() == null){
            DeliveryDto dtoWithDeliveryCost = deliveryClient.calculateDeliveryCost(toDto(order, productMap));
            order.setDeliveryPrice(dtoWithDeliveryCost.getDeliveryCost());
        }
        if (order.getProductPrice() == null){
            PaymentDto dtoWithProductCost = paymentClient.calculateProductCost(toDto(order, productMap));
            order.setProductPrice(dtoWithProductCost.getProductCost());
        }
        PaymentDto payment = paymentClient.calculateTotalCost(toDto(order, productMap));
        if (payment.getTotalCost() == null) {
            throw new PaymentClientException("произошла ошибка при подсчете итоговой стоимости заказа");
        }
        order.setTotalPrice(payment.getTotalCost());
        orderRepository.save(order);
        return toDto(order, productMap);
    }

    @Override
    public OrderDto calculateProductCost(String orderId) {
        Order order = getOrderById(orderId);
        List<OrderProduct> products = orderProductRepository.findAllByOrderId(orderId);
        if (products == null || products.isEmpty()) {
            throw new NotFoundException("для заказа " + orderId + " не найдено ни одного товара");
        }
        Map<String, Integer> productMap = products.stream().collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));
        PaymentDto paymentWithProductCost = paymentClient.calculateProductCost(toDto(order, productMap));
        if (paymentWithProductCost.getProductCost() == null) {
            throw new PaymentClientException("произошла ошибка при подсчете стоимости товаров заказа");
        }
        order.setProductPrice(paymentWithProductCost.getProductCost());
        orderRepository.save(order);
        return toDto(order, productMap);
    }

    @Override
    public OrderDto calculateDeliveryCost(String orderId) {
        Order order = getOrderById(orderId);
        List<OrderProduct> products = orderProductRepository.findAllByOrderId(orderId);
        if (products == null || products.isEmpty()) {
            throw new NotFoundException("для заказа " + orderId + " не найдено ни одного товара");
        }
        Map<String, Integer> productMap = products.stream().collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));
        DeliveryDto dtoWithDeliveryCost = deliveryClient.calculateDeliveryCost(toDto(order, productMap));
        if (dtoWithDeliveryCost.getDeliveryCost() == null){
            throw new DeliveryClientException("произошла ошибка при подсчете стоимости доставки");
        }
        order.setDeliveryPrice(dtoWithDeliveryCost.getDeliveryCost());
        orderRepository.save(order);
        return toDto(order, productMap);
    }

    private Order getOrderById(String orderId) {
        return orderRepository.findById(UUID.fromString(orderId)).orElseThrow(() ->
                new NotFoundException("заказ с id " + orderId + " не найден"));
    }
}
