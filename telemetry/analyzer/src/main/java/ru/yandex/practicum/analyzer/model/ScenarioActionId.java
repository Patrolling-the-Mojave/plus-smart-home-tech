package ru.yandex.practicum.analyzer.model;

import lombok.Data;

@Data
public class ScenarioActionId {
    private Long scenario;
    private String sensor;
    private Long action;
}
