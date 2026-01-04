package ru.yandex.practicum.commerce.shoppingstore.mapper;

import ru.yandex.practicum.commerce.dto.product.ProductDto;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;

import java.util.List;

public class ProductMapper {
    public static Product toEntity(ProductDto product) {
        return Product.builder()
                .id(product.getId())
                .description(product.getDescription())
                .productName(product.getProductName())
                .price(product.getPrice())
                .productCategory(product.getProductCategory())
                .productState(product.getProductState())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .build();
    }

    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .description(product.getDescription())
                .productName(product.getProductName())
                .price(product.getPrice())
                .productCategory(product.getProductCategory())
                .productState(product.getProductState())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .build();
    }

    private static List<ProductDto> toDto(List<Product> products) {
        return products.stream().map(ProductMapper::toDto).toList();
    }
}
