package ru.yandex.practicum.commerce.shoppingstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.dto.product.ProductQuantityDto;

public interface ProductService {

    ProductDto addProduct(ProductDto newProduct);

    ProductDto findProductById(String id);

    Page<ProductDto> findAllByCategory(ProductCategory productCategory, Pageable pageable);

    ProductDto updateProduct(ProductDto updatedProduct);

    Boolean deleteProductById(String id);

    Boolean setProductQuantity(ProductQuantityDto newQuantity);

}
