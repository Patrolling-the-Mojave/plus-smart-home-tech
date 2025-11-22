package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;

import static ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapper.toAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorEventService {
    private final EventProducer eventProducer;

    @Value("${spring.kafka.topics.telemetry.sensors.v1}")
    private String sensorTopic;

    public SensorEvent handleSensorEvent(SensorEvent sensorEvent) {
        eventProducer.send(toAvro(sensorEvent), sensorTopic, sensorEvent.getId());
        log.debug("сообщение{} успешно отправлено в топик{}", sensorEvent, sensorTopic);
        return sensorEvent;
    }
}
