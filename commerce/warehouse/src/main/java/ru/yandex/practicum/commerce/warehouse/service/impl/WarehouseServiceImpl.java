package ru.yandex.practicum.commerce.warehouse.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.cart.CartProductDto;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.commerce.warehouse.model.Product;
import ru.yandex.practicum.commerce.warehouse.repository.ProductRepository;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

    @Override
    public void addNewProduct(NewProductInWarehouseRequest newProduct) {
        if (productRepository.existsById(newProduct.getProductId())){
            throw new SpecifiedProductAlreadyInWarehouseException("товар с id "+ newProduct.getProductId()+" уже есть на складе");
        }
        Product product = ProductMapper.toEntity(newProduct);
        productRepository.save(product);
        log.debug("сохранена информация о товаре{}", product);
    }

    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto cart) {
        List<String> productIds = cart.getProducts().stream().map(CartProductDto::getProductId).toList();
        List<Product> products = productRepository.findAllByProductIdIn(productIds);
        Map<String, Product> productById = products.stream().collect(Collectors.toMap(Product::getProductId, Function.identity()));
        Double volume = 0.0;
        Double weight = 0.0;
        Boolean fragile = false;
        for (CartProductDto cartProduct : cart.getProducts()) {
            Product product = productById.get(cartProduct.getProductId());
            if (product == null){
                throw new NoSpecifiedProductInWarehouseException("товар с id "+ cartProduct.getProductId()+" не найден на складе");
            }
            if (product.getQuantity() < cartProduct.getQuantity()) {
                throw new NoSpecifiedProductInWarehouseException("нет нужно количества товара " + cartProduct.getProductId() + " на складе");
            }
            if (!fragile) {
                fragile = product.getFragile();
            }
            volume += product.getWidth() * product.getHeight() * product.getDepth() * cartProduct.getQuantity();
            weight += product.getWeight() * cartProduct.getQuantity();
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

    private Product getProductById(String id) {
        return productRepository.findById(id).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException("продукт c id " + id + " не найден на складе"));
    }
}
