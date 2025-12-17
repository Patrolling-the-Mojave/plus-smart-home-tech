package ru.yandex.practicum.analyzer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScenarioConditionId implements Serializable {
    private Long scenario;
    private String sensor;
    private Long condition;
}
