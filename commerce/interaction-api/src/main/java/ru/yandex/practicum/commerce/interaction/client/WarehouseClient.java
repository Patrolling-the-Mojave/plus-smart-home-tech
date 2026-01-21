package ru.yandex.practicum.commerce.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.interaction.client.fallback.WarehouseClientFallback;

@FeignClient(name = "warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseClient {

    @PutMapping("/api/v1/warehouse")
    void addNewProduct(@RequestBody NewProductInWarehouseRequest newProduct);

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkShoppingCart(@RequestBody ShoppingCartDto cart);

    @PostMapping("/api/v1/warehouse/add")
    void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/api/v1/warehouse/collect")
    BookedProductsDto collectOrderProducts(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/warehouse/delivery-link")
    void addDeliveryToAssembledOrder(@RequestParam String orderId, @RequestParam String deliveryId);

    @PostMapping("/api/v1/warehouse/return")
    void returnProducts(@RequestBody ProductReturnRequest returnRequest);
}



