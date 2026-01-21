package ru.yandex.practicum.commerce.interaction.client.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.exception.WarehouseUnavailableException;
import ru.yandex.practicum.commerce.interaction.client.WarehouseClient;

@Component
public class WarehouseClientFallback implements WarehouseClient {

    @Override
    public void addNewProduct(NewProductInWarehouseRequest newProduct) {
        throw new WarehouseUnavailableException("Склад временно недоступен");
    }

    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto cart) {
        throw new WarehouseUnavailableException("Невозможно проверить наличие товаров: склад недоступен");
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        throw new WarehouseUnavailableException("Склад временно недоступен");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return AddressDto.builder()
                .country("UNKNOWN")
                .city("UNKNOWN")
                .street("UNKNOWN")
                .build();
    }

    @Override
    public void addDeliveryToAssembledOrder(String orderId, String deliveryId) {
        throw new WarehouseUnavailableException("сервис доставки временно недоступен");
    }

    @Override
    public BookedProductsDto collectOrderProducts(OrderDto orderDto) {
        throw new WarehouseUnavailableException("сборка заказа временно недоступна");
    }

    @Override
    public void returnProducts(ProductReturnRequest returnRequest) {
        throw new WarehouseUnavailableException("возврат товаров временно недоступен");
    }
}
