package ru.yandex.practicum.commerce.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

@FeignClient(name = "delivery")
public interface DeliveryClient {

    @PostMapping("/api/v1/delivery/cost")
    DeliveryDto calculateDeliveryCost(@RequestBody OrderDto orderDto);

    @PutMapping("/api/v1/delivery")
    DeliveryDto prepareDelivery(@RequestBody DeliveryDto deliveryDto);

    @PostMapping("/api/v1/delivery/successful")
    void setToSuccessfulStatus(@RequestBody String orderId);

    @PostMapping("/api/v1/delivery/failed")
    void setToUnsuccessfulStatus(@RequestBody String orderId);

    @PostMapping("/api/v1/delivery/picked")
    void addOrderToDelivery(@RequestBody String orderId);

}
