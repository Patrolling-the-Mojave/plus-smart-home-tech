package ru.yandex.practicum.commerce.shoppingstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.w3c.dom.stylesheets.LinkStyle;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.dto.product.ProductQuantityDto;
import ru.yandex.practicum.commerce.dto.product.QuantityState;

import java.util.List;

public interface ProductService {

    ProductDto addProduct(ProductDto newProduct);

    ProductDto findProductById(String id);

    List<ProductDto> findAllByCategory(ProductCategory productCategory);

    ProductDto updateProduct(ProductDto updatedProduct);

    Boolean deleteProductById(String id);

    Boolean setProductQuantity(String productId, QuantityState quantity);

}
