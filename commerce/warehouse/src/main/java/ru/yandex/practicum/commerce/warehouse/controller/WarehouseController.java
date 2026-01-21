package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.interaction.client.WarehouseClient;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseClient {
    private final WarehouseService warehouseService;

    @PutMapping
    @Override
    public void addNewProduct(@Validated @RequestBody NewProductInWarehouseRequest newProduct) {
        warehouseService.addNewProduct(newProduct);
    }

    @PostMapping("/check")
    @Override
    public BookedProductsDto checkShoppingCart(@RequestBody ShoppingCartDto cart) {
        return warehouseService.checkShoppingCart(cart);
    }

    @PostMapping("/add")
    @Override
    public void addProductToWarehouse(@Validated @RequestBody AddProductToWarehouseRequest request) {
        warehouseService.addProductToWarehouse(request);
    }

    @GetMapping("/address")
    @Override
    public AddressDto getWarehouseAddress(){
        return warehouseService.getWarehouseAddress();
    }

    @PostMapping("/delivery-link")
    @Override
    public void addDeliveryToAssembledOrder(@RequestParam String orderId, @RequestParam String deliveryId) {
        warehouseService.addOrderToDelivery(orderId, deliveryId);
    }

    @Override
    @PostMapping("/collect")
    public BookedProductsDto collectOrderProducts(@RequestBody OrderDto orderDto) {
        return warehouseService.collectOrderedProducts(orderDto);
    }

    @Override
    @PostMapping("/return")
    public void returnProducts(@RequestBody ProductReturnRequest returnRequest) {
        warehouseService.returnProducts(returnRequest);
    }
}
