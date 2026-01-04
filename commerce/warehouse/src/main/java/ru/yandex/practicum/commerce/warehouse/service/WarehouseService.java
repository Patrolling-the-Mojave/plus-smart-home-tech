package ru.yandex.practicum.commerce.warehouse.service;

import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseService {

    void addNewProduct(NewProductInWarehouseRequest newProduct);

    BookedProductsDto checkShoppingCart(ShoppingCartDto cart);

    void addProductToWarehouse(AddProductToWarehouseRequest addProductsRequest);

    AddressDto getWarehouseAddress();



}
