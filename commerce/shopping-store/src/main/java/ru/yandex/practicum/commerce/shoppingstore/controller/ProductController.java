package ru.yandex.practicum.commerce.shoppingstore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.dto.product.ProductQuantityDto;
import ru.yandex.practicum.commerce.dto.product.QuantityState;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public List<ProductDto> findProductByCategory(@RequestParam ProductCategory category) {
        return productService.findAllByCategory(category);
    }

    @PutMapping
    public ProductDto addProduct(@Validated @RequestBody ProductDto newProduct) {
        return productService.addProduct(newProduct);
    }

    @PostMapping
    public ProductDto updateProduct(@Validated @RequestBody ProductDto updatedProduct) {
        return productService.updateProduct(updatedProduct);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProduct(@RequestBody String productId) {
        return productService.deleteProductById(productId);
    }

    @PostMapping("/quantityState")
    public Boolean setQuantity(@RequestParam String productId, @RequestParam QuantityState quantityState) {
        return productService.setProductQuantity(productId, quantityState);
    }

    @GetMapping("/{productId}")
    public ProductDto findProductById(@PathVariable String productId) {
        return productService.findProductById(productId);
    }
}
