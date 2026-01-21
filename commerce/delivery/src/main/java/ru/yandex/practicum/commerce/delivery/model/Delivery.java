package ru.yandex.practicum.commerce.delivery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryStatus;

import java.math.BigDecimal;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "delivery")
public class Delivery {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "delivery_cost")
    private BigDecimal deliveryCost;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "total_weight")
    private Double totalWeight;

    @Column(name = "total_volume")
    private Double totalVolume;

    @Column(name = "fragile")
    private Boolean fragile = false;

    @Column(name = "origin_country")
    private String originCountry;

    @Column(name = "origin_city")
    private String originCity;

    @Column(name = "origin_street")
    private String originStreet;

    @Column(name = "origin_house")
    private String originHouse;

    @Column(name = "origin_flat")
    private String originFlat;

    @Column(name = "destination_country")
    private String destinationCountry;

    @Column(name = "destination_city")
    private String destinationCity;

    @Column(name = "destination_street")
    private String destinationStreet;

    @Column(name = "destination_house")
    private String destinationHouse;

    @Column(name = "destination_flat")
    private String destinationFlat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeliveryStatus status;

}
