package ru.yandex.practicum.telemetry.collector.model.hub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScenarioCondition {
    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private Object value;
}
