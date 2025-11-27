package ru.yandex.practicum.telemetry.collector.service.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventProtoMapper;

@RequiredArgsConstructor
@Component
public class LightSensorEventHandler implements SensorEventHandler {
    private final SensorEventService sensorEventService;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto sensorEventProto) {
        sensorEventService.handleSensorEvent(SensorEventProtoMapper.toSensorEvent(sensorEventProto));
    }
}

