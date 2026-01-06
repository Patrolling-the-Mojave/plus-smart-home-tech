package ru.yandex.practicum.commerce.shoppingstore.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.product.*;
import ru.yandex.practicum.commerce.shoppingstore.exception.NotFoundException;
import ru.yandex.practicum.commerce.shoppingstore.mapper.ProductMapper;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import ru.yandex.practicum.commerce.shoppingstore.repository.ProductRepository;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductService;

import java.util.List;

import static ru.yandex.practicum.commerce.shoppingstore.mapper.ProductMapper.toDto;
import static ru.yandex.practicum.commerce.shoppingstore.mapper.ProductMapper.toEntity;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public ProductDto addProduct(ProductDto newProduct) {
        Product product = productRepository.save(toEntity(newProduct));
        log.debug("сохранен новый товар{}", newProduct);
        return toDto(product);
    }

    @Override
    public ProductDto findProductById(String id) {
        Product product = getProductById(id);
        log.debug("найден товар{}", product);
        return toDto(product);

    }

    @Override
    public List<ProductDto> findAllByCategory(ProductCategory productCategory) {
        List<Product> products = productRepository.findAllByProductCategory(productCategory);
        log.debug("найдено товаров {} для категории {}", products.size(), productCategory);
        return toDto(products);
    }

    @Override
    public ProductDto updateProduct(ProductDto updatedProduct) {
        Product product = productRepository.save(toEntity(updatedProduct));
        log.debug("товар обновлен{}", updatedProduct);
        return toDto(product);
    }

    @Override
    @Transactional
    public Boolean deleteProductById(String id) {
        Product product = getProductById(id);
        log.debug("найден товар{}", product);
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return Boolean.TRUE;
    }

    @Override
    public Boolean setProductQuantity(String productId, QuantityState quantityState) {
        Product product = getProductById(productId);
        product.setQuantityState(quantityState);
        productRepository.save(product);
        return Boolean.TRUE;
    }


    private Product getProductById(String id) {
        return productRepository.findById(id).orElseThrow(() ->
                new NotFoundException("товар с id " + id + " не найден"));
    }

}
