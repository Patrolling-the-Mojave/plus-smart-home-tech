package ru.yandex.practicum.commerce.shoppingstore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.dto.product.QuantityState;
import ru.yandex.practicum.commerce.interaction.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ProductController implements ShoppingStoreClient {
    private final ProductService productService;

    @GetMapping
    @Override
    public Page<ProductDto> findProductByCategory(@RequestParam ProductCategory category,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "150") int size,
                                                  @RequestParam(defaultValue = "productName") String sort
    ) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return productService.findAllByCategory(category, pageable);
    }

    @PutMapping
    @Override
    public ProductDto addProduct(@Validated @RequestBody ProductDto newProduct) {
        return productService.addProduct(newProduct);
    }

    @PostMapping
    @Override
    public ProductDto updateProduct(@Validated @RequestBody ProductDto updatedProduct) {
        return productService.updateProduct(updatedProduct);
    }

    @PostMapping("/removeProductFromStore")
    @Override
    public Boolean removeProduct(@RequestBody String productId) {
        return productService.deleteProductById(productId.replaceAll("^\"|\"$", ""));
    }

    @PostMapping("/quantityState")
    @Override
    public Boolean setQuantity(@RequestParam String productId, @RequestParam QuantityState quantityState) {
        return productService.setProductQuantity(productId, quantityState);
    }

    @GetMapping("/{productId}")
    @Override
    public ProductDto findProductById(@PathVariable String productId) {
        return productService.findProductById(productId);
    }

    @PostMapping("/find-by-ids")
    @Override
    public List<ProductDto> findAllByProductIds(@RequestBody List<String> productIds) {
        return productService.findByProductIds(productIds);
    }
}
