package ru.yandex.practicum.telemetry.collector.model.hub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceAction {
    private String sensorId;
    private ActionType type;
    private Integer value;
}
