package ru.yandex.practicum.telemetry.collector.model.sensor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LightSensorEvent extends SensorEvent{
    private Integer linkQuality;
    private Integer luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
