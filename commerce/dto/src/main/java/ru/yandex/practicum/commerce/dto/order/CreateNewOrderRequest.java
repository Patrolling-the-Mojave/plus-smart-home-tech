package ru.yandex.practicum.commerce.dto.order;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;

@Data
@Builder
public class CreateNewOrderRequest {
    private ShoppingCartDto shoppingCart;
    private AddressDto deliveryAddress;

}
