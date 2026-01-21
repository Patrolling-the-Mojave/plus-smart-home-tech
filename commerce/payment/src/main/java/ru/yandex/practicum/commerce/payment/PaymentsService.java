package ru.yandex.practicum.commerce.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "ru.yandex.practicum.commerce.interaction.client")
@SpringBootApplication
public class PaymentsService {
    public static void main(String[] args) {
        SpringApplication.run(PaymentsService.class, args);
    }
}
