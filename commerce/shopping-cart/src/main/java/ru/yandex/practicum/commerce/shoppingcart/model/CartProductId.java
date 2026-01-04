package ru.yandex.practicum.commerce.shoppingcart.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CartProductId implements Serializable {
    private String cartId;
    private String productId;
}
