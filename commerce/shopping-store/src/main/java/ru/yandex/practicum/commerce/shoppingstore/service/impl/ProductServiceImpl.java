package ru.yandex.practicum.commerce.shoppingstore.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.dto.product.ProductQuantityDto;
import ru.yandex.practicum.commerce.shoppingstore.exception.NotFoundException;
import ru.yandex.practicum.commerce.shoppingstore.mapper.ProductMapper;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import ru.yandex.practicum.commerce.shoppingstore.repository.ProductRepository;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductService;

import static ru.yandex.practicum.commerce.shoppingstore.mapper.ProductMapper.toDto;
import static ru.yandex.practicum.commerce.shoppingstore.mapper.ProductMapper.toEntity;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public ProductDto addProduct(ProductDto newProduct) {
        productRepository.save(toEntity(newProduct));
        log.debug("сохранен новый товар{}", newProduct);
        return newProduct;
    }

    @Override
    public ProductDto findProductById(String id) {
        Product product = getProductById(id);
        log.debug("найден товар{}", product);
        return toDto(product);

    }

    @Override
    public Page<ProductDto> findAllByCategory(ProductCategory productCategory, Pageable pageable) {
        Page<Product> products = productRepository.findAllByProductCategory(productCategory, pageable);
        log.debug("найдено товаров {} для категории {}", products.getSize(), productCategory);
        return products.map(ProductMapper::toDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto updatedProduct) {
        productRepository.save(toEntity(updatedProduct));
        log.debug("товар обновлен{}", updatedProduct);
        return updatedProduct;
    }

    @Override
    public Boolean deleteProductById(String id) {
        Product product = getProductById(id);
        log.debug("найден товар{}", product);
        productRepository.delete(product);
        return Boolean.TRUE;
    }

    @Override
    public Boolean setProductQuantity(ProductQuantityDto newQuantity) {
        Product product = getProductById(newQuantity.getId());
        product.setQuantityState(newQuantity.getQuantityState());
        productRepository.save(product);
        return Boolean.TRUE;
    }


    private Product getProductById(String id) {
        return productRepository.findById(id).orElseThrow(() ->
                new NotFoundException("товар с id " + id + " не найден"));
    }

}
