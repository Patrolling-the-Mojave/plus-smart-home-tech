package ru.yandex.practicum.commerce.warehouse.mapper;

import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.Product;

public class ProductMapper {

    public static Product toEntity(NewProductInWarehouseRequest newProduct){
        return Product.builder()
                .productId(newProduct.getProductId())
                .fragile(newProduct.getFragile())
                .height(newProduct.getDimension().getHeight())
                .width(newProduct.getDimension().getWidth())
                .depth(newProduct.getDimension().getDepth())
                .weight(newProduct.getWeight())
                .quantity(0)
                .build();
    }

}
