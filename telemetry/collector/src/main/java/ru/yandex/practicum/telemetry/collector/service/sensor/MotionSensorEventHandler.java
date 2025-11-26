package ru.yandex.practicum.telemetry.collector.service.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventProtoMapper;

@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {
    private final SensorEventService sensorEventService;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public void handle(SensorEventProto sensorEventProto) {
        sensorEventService.handleSensorEvent(SensorEventProtoMapper.toSensorEvent(sensorEventProto));
    }
}
