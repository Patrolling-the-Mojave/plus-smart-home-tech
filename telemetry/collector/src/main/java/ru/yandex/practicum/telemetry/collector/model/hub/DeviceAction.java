package ru.yandex.practicum.telemetry.collector.model.hub;

import lombok.Data;

@Data
public class DeviceAction {
    private String sensorId;
    private ActionType type;
    private Integer value;
}
