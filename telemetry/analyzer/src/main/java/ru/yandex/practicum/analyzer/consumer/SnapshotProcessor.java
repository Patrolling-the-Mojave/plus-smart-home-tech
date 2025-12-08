package ru.yandex.practicum.analyzer.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.exception.AnalyzerException;
import ru.yandex.practicum.analyzer.grpcservice.HubRouterService;
import ru.yandex.practicum.analyzer.model.*;
import ru.yandex.practicum.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotKafkaConsumer;
    private final ScenarioRepository scenarioRepository;
    private final HubRouterService hubRouterService;
    private final SensorRepository sensorRepository;
    private static final Duration POLL_DURATION = Duration.ofMillis(5000);

    @Value("${spring.kafka.topics.telemetry.snapshots.v1}")
    private String snapshotTopic;

    @Override
    public void run() {
        snapshotKafkaConsumer.subscribe(List.of(snapshotTopic));
        Runtime.getRuntime().addShutdownHook(new Thread(snapshotKafkaConsumer::wakeup));
        try {
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotKafkaConsumer.poll(POLL_DURATION);
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    processSnapshot(record.value());
                }
                snapshotKafkaConsumer.commitSync();
            }
        } catch (WakeupException exception) {

        } catch (Exception exception) {
            log.error("произошла ошибка при обработке записей", exception);
            throw new AnalyzerException("произошла ошибка при обработке записей", exception);
        }
    }

    @Transactional
    private void processSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.info("Получен снапшот для хаба: {}", hubId);
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        log.info("Найдено сценариев для хаба {}: {}", hubId, scenarios.size());
        for (Scenario scenario : scenarios) {
            if (isScenarioTriggered(scenario, snapshot.getSensorsState())) {
                log.info("Сценарий '{}' активирован Отправляем команду в Hub Router", scenario.getName());
                hubRouterService.sendDeviceActionRequest(scenario);
            }
        }
    }

    private boolean isScenarioTriggered(Scenario scenario, Map<String, SensorStateAvro> states) {
        return scenario.getScenarioConditions().stream().allMatch(scenarioCondition -> {
            Sensor sensor = scenarioCondition.getSensor();
            SensorStateAvro state = states.get(sensor.getId());
            if (state == null) {
                return false;
            }
            if (!scenario.getHubId().equals(sensor.getHubId())) {
                log.warn("Датчик {} принадлежит другому хабу ({} != {})",
                        sensor.getId(), sensor.getHubId(), scenario.getHubId());
                return false;
            }
            Condition condition = scenarioCondition.getCondition();
            Integer value = extractValueFromSensorStateData(state.getData(), condition.getType());
            if (value == null) {
                return false;
            }
            return checkCondition(value, condition.getOperation(), condition.getValue());
        });
    }

    private boolean checkCondition(Integer currentValue, ConditionOperation conditionOperation, Integer value) {
        return switch (conditionOperation) {
            case EQUALS -> currentValue.equals(value);
            case GREATER_THAN -> currentValue > value;
            case LOWER_THAN -> currentValue < value;
        };
    }

    private Integer extractValueFromSensorStateData(Object data, ConditionType conditionType) {
        switch (conditionType) {
            case LUMINOSITY -> {
                if (data instanceof LightSensorAvro lightSensor) {
                    return lightSensor.getLuminosity();
                }
            }
            case TEMPERATURE -> {
                if (data instanceof ClimateSensorAvro climateSensor) {
                    return climateSensor.getTemperatureC();
                } else if (data instanceof TemperatureSensorAvro temperatureSensor) {
                    return temperatureSensor.getTemperatureC();
                }
            }
            case MOTION -> {
                if (data instanceof MotionSensorAvro motionSensor) {
                    return motionSensor.getMotion() ? 1 : 0;
                }
            }
            case SWITCH -> {
                if (data instanceof SwitchSensorAvro switchSensor) {
                    return switchSensor.getState() ? 1 : 0;
                }
            }
            case CO2LEVEL -> {
                if (data instanceof ClimateSensorAvro climateSensor) {
                    return climateSensor.getCo2Level();
                }
            }
            case HUMIDITY -> {
                if (data instanceof ClimateSensorAvro climateSensor) {
                    return climateSensor.getHumidity();
                }
            }
            default -> {
                log.warn("тип{} условия не найден", conditionType);
                return null;
            }
        }
        return null;
    }
}
