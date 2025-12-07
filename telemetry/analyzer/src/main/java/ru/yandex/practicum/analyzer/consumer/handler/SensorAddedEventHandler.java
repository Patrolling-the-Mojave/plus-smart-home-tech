package ru.yandex.practicum.analyzer.consumer.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.exception.AnalyzerException;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SensorAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public HandlerType getType() {
        return HandlerType.SENSOR_ADDED;
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {
        try {
            DeviceAddedEventAvro deviceAddedEvent = (DeviceAddedEventAvro) hubEventAvro.getPayload();
            String hubId = hubEventAvro.getHubId();
            String deviceId = deviceAddedEvent.getId();

            Optional<Sensor> existingSensor = sensorRepository.findById(deviceId);
            if (existingSensor.isPresent()) {
                log.info("устройство  {} уже существует", deviceId);
                Sensor sensor = existingSensor.get();
                sensor.setHubId(hubId);
                sensorRepository.save(sensor);
            } else {
                Sensor sensor = Sensor.builder()
                        .id(deviceId)
                        .hubId(hubId)
                        .build();
                sensorRepository.save(sensor);
            }

        } catch (Exception e) {
            log.error("ошибка при добавлении устройства: {}", e.getMessage(), e);
            throw new AnalyzerException("Ошибка добавления устройства", e);
        }
    }
}
