package ru.yandex.practicum.commerce.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.dto.product.ProductQuantityDto;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/api/v1/shopping-store")
    Page<ProductDto> findProductByCategory(@RequestParam("category") ProductCategory category,
                                           @RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam(value = "sort", required = false) String sort);

    @PutMapping("/api/v1/shopping-store")
    ProductDto addProduct(@RequestBody ProductDto newProduct);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody ProductDto updatedProduct);

    @PostMapping("/api/v1/shopping-store/removeProductFromStorage")
    Boolean removeProduct(@RequestBody String productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    Boolean setQuantity(@RequestBody ProductQuantityDto newQuantity);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto findProductById(@PathVariable String productId);

}
