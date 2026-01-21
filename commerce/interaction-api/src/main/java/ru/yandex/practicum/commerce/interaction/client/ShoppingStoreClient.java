package ru.yandex.practicum.commerce.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.dto.product.ProductQuantityDto;
import ru.yandex.practicum.commerce.dto.product.QuantityState;

import java.util.List;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/api/v1/shopping-store")
    Page<ProductDto> findProductByCategory(@RequestParam ProductCategory category,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "150") int size,
                                           @RequestParam(defaultValue = "productName") String sort);

    @PutMapping("/api/v1/shopping-store")
    ProductDto addProduct(@RequestBody ProductDto newProduct);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody ProductDto updatedProduct);

    @PostMapping("/api/v1/shopping-store/removeProductFromStorage")
    Boolean removeProduct(@RequestBody String productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    Boolean setQuantity(@RequestParam String productId, @RequestParam QuantityState quantityState);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto findProductById(@PathVariable String productId);

    @PostMapping("/api/v1/shopping-store/find-by-ids")
    List<ProductDto> findAllByProductIds(@RequestBody List<String> productIds);

}
