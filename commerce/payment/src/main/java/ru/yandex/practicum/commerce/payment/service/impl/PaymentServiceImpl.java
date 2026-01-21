package ru.yandex.practicum.commerce.payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentStatus;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.interaction.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.payment.exception.NoDeliveryCostException;
import ru.yandex.practicum.commerce.payment.exception.NotFoundException;
import ru.yandex.practicum.commerce.payment.exception.ProductCostIsNotCalculatedException;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;
import ru.yandex.practicum.commerce.payment.service.PaymentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.yandex.practicum.commerce.payment.mapper.PaymentMapper.toDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private static final BigDecimal NDS_PERCENT = new BigDecimal("10.00");

    @Override
    @Transactional
    public PaymentDto calculateProductCost(OrderDto orderDto) {
        Payment payment = getPaymentById(orderDto.getPaymentId());

        Map<String, Integer> productCount = orderDto.getProducts();

        List<ProductDto> products = shoppingStoreClient.findAllByProductIds(new ArrayList<>(productCount.keySet()));
        if (products == null || products.isEmpty()) {
            throw new NotFoundException("Не найдены цены для товаров");
        }
        Map<String, BigDecimal> productPriceMap = products.stream().collect(Collectors.toMap(ProductDto::getProductId, ProductDto::getPrice));

        BigDecimal totalProductCost = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : productCount.entrySet()) {
            String productId = entry.getKey();
            Integer quantity = entry.getValue();
            BigDecimal price = productPriceMap.get(productId);

            if (price == null) {
                throw new IllegalArgumentException("не найдена цена для товара с id " + productId);
            }

            BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
            totalProductCost = totalProductCost.add(total);
        }
        payment.setProductCost(totalProductCost);
        return toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentDto preparePayment(OrderDto orderDto) {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID().toString())
                .orderId(orderDto.getOrderId())
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
        return toDto(payment);
    }

    @Override
    @Transactional
    public PaymentDto calculateTotalCost(OrderDto orderDto) {
        Payment payment = getPaymentById(orderDto.getPaymentId());
        if (payment.getProductCost() == null){
            throw new ProductCostIsNotCalculatedException("стоимость продуктов еще не рассчитана");
        }
        if (orderDto.getDeliveryPrice() == null){
            throw new NoDeliveryCostException("стоимость доставки не рассчитана");
        }
        BigDecimal feeTotal = payment.getProductCost()
                .multiply(NDS_PERCENT)
                .divide(BigDecimal.valueOf(100), 2 , RoundingMode.HALF_UP);
        BigDecimal totalCost = payment.getProductCost().add(feeTotal).add(orderDto.getDeliveryPrice());
        payment.setFeeTotal(feeTotal);
        payment.setTotalCost(totalCost);
        return toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentDto setToSuccessfulPayment(OrderDto orderDto) {
        Payment payment = getPaymentById(orderDto.getPaymentId());
        payment.setStatus(PaymentStatus.SUCCESS);
        return toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentDto setToUnsuccessfulPayment(OrderDto orderDto) {
        Payment payment = getPaymentById(orderDto.getPaymentId());
        payment.setStatus(PaymentStatus.FAILED);
        return toDto(paymentRepository.save(payment));
    }

    private Payment getPaymentById(String id) {
        return paymentRepository.findById(id).orElseThrow(() ->
                new NotFoundException("платеж с id " + id + " не найден"));
    }


}
