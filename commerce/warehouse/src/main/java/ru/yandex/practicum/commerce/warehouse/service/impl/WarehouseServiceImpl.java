package ru.yandex.practicum.commerce.warehouse.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.commerce.warehouse.model.Product;
import ru.yandex.practicum.commerce.warehouse.model.ReservedProduct;
import ru.yandex.practicum.commerce.warehouse.repository.ProductRepository;
import ru.yandex.practicum.commerce.warehouse.repository.ReservedProductRepository;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    private final ProductRepository productRepository;
    private final ReservedProductRepository reservedProductRepository;

    @Override
    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest newProduct) {
        if (productRepository.existsById(newProduct.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("товар с id " + newProduct.getProductId() + " уже есть на складе");
        }
        Product product = ProductMapper.toEntity(newProduct);
        productRepository.save(product);
        log.debug("сохранена информация о товаре{}", product);
    }

    @Override
    @Transactional
    public BookedProductsDto checkShoppingCart(ShoppingCartDto cart) {
        Set<String> productIds = cart.getProducts().keySet();
        List<Product> products = productRepository.findAllByProductIdIn(productIds);
        Map<String, Product> productById = products.stream().collect(Collectors.toMap(Product::getProductId, Function.identity()));
        Double volume = 0.0;
        Double weight = 0.0;
        Boolean fragile = false;
        for (String productId : cart.getProducts().keySet()) {
            Product product = productById.get(productId);
            if (product == null) {
                throw new NoSpecifiedProductInWarehouseException("товар с id " + productId + " не найден на складе");
            }
            if (product.getQuantity() < cart.getProducts().get(productId)) {
                throw new NoSpecifiedProductInWarehouseException("нет нужно количества товара " + productId + " на складе");
            }
            if (!fragile) {
                fragile = product.getFragile();
            }
            volume += product.getWidth() * product.getHeight() * product.getDepth() * cart.getProducts().get(productId);
            weight += product.getWeight() * cart.getProducts().get(productId);
        }
        log.debug("финальные данные о заказе volume{}, weight{}, fragile{}", volume, weight, fragile);
        return createBookedProducts(fragile, weight, volume);
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest addProductsRequest) {
        Product product = getProductById(addProductsRequest.getProductId());
        if (addProductsRequest.getQuantity() < 0) {
            throw new IllegalArgumentException("указано отрицательное количество для товара " + addProductsRequest.getProductId());
        }
        product.setQuantity(product.getQuantity() + addProductsRequest.getQuantity());
        productRepository.save(product);
        log.debug("товар {} добавлен на склад в количестве{}", product.getProductId(), addProductsRequest.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.debug("запрос на получение адреса склада");
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    private BookedProductsDto createBookedProducts(Boolean fragile, Double weight, Double volume) {
        return BookedProductsDto.builder()
                .fragile(fragile)
                .deliveryVolume(volume)
                .deliveryWeight(weight)
                .build();
    }

    @Override
    @Transactional
    public void addOrderToDelivery(String orderId, String deliveryId) {
        List<ReservedProduct> reservedProducts = reservedProductRepository.findAllByOrderId(orderId);
        for (ReservedProduct product : reservedProducts) {
            product.setDeliveryId(deliveryId);
        }
        reservedProductRepository.saveAll(reservedProducts);
    }

    @Override
    @Transactional
    public BookedProductsDto collectOrderedProducts(OrderDto orderDto) {
        Set<String> productIds = orderDto.getProducts().keySet();
        List<Product> warehouseProducts = productRepository.findAllByProductIdIn(productIds);
        Map<String, Product> productMap = warehouseProducts.stream().collect(Collectors.toMap(Product::getProductId, Function.identity()));
        Double volume = 0.0;
        Double weight = 0.0;
        Boolean fragile = false;
        List<ReservedProduct> reservedProducts = new ArrayList<>();
        List<Product> updatedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product product = productMap.get(productId);
            if (product == null) {
                throw new NoSpecifiedProductInWarehouseException("товар с id " + productId + " не найден на складе");
            }
            if (product.getQuantity() < orderDto.getProducts().get(productId)) {
                throw new NoSpecifiedProductInWarehouseException("нет нужно количества товара " + productId + " на складе");
            }
            if (!fragile) {
                fragile = product.getFragile();
            }
            volume += product.getWidth() * product.getHeight() * product.getDepth() * orderDto.getProducts().get(productId);
            weight += product.getWeight() * orderDto.getProducts().get(productId);
            ReservedProduct reservedProduct = ReservedProduct.builder()
                    .id(UUID.randomUUID().toString())
                    .orderId(orderDto.getOrderId())
                    .productId(productId)
                    .quantity(orderDto.getProducts().get(productId))
                    .build();
            reservedProducts.add(reservedProduct);
            product.setQuantity(product.getQuantity() - orderDto.getProducts().get(productId));
            updatedProducts.add(product);
        }
        log.debug("финальные данные о заказе volume{}, weight{}, fragile{}", volume, weight, fragile);
        reservedProductRepository.saveAll(reservedProducts);
        productRepository.saveAll(updatedProducts);
        return createBookedProducts(fragile, weight, volume);
    }

    @Override
    @Transactional
    public void returnProducts(ProductReturnRequest returnRequest) {
        Set<String> productIds = returnRequest.getProducts().keySet();
        List<Product> products = productRepository.findAllByProductIdIn(productIds);
        Map<String, Product> productMap = products.stream().collect(Collectors.toMap(Product::getProductId, Function.identity()));
        for (String productId : productIds) {
            Product product = getProductById(productId);
            if (product == null) {
                throw new NoSpecifiedProductInWarehouseException("товар с id " + productId + " не найден на складе");
            }
            product.setQuantity(product.getQuantity() + returnRequest.getProducts().get(productId));
        }
        productRepository.saveAll(products);
    }

    private Product getProductById(String id) {
        return productRepository.findById(id).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException("продукт c id " + id + " не найден на складе"));
    }
}
