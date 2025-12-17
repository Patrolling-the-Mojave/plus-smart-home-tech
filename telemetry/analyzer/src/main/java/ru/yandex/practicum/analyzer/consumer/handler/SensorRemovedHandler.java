package ru.yandex.practicum.analyzer.consumer.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.exception.AnalyzerException;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SensorRemovedHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;

    @Override
    public HandlerType getType() {
        return HandlerType.SENSOR_REMOVED;
    }

    @Override
    @Transactional
    public void handle(HubEventAvro hubEventAvro) {
        try {
            DeviceRemovedEventAvro deviceRemovedEvent = (DeviceRemovedEventAvro) hubEventAvro.getPayload();
            String deviceId = deviceRemovedEvent.getId();

            Optional<Sensor> sensor = sensorRepository.findById(deviceId);
            if (sensor.isEmpty()) {
                log.warn("Устройство {} не найдено", deviceId);
                return;
            }
            List<Scenario> scenarios = scenarioRepository.findBySensorId(deviceId);
            for (Scenario scenario : scenarios) {
                removeSensorFromScenario(scenario, deviceId);
            }
            sensorRepository.deleteById(deviceId);
            log.info("устройство {} успешно удалено", deviceId);
        } catch (Exception e) {
            log.error("Ошибка при удалении устройства: {}", e.getMessage(), e);
            throw new AnalyzerException("Ошибка удаления устройства", e);
        }
    }

    private void removeSensorFromScenario(Scenario scenario, String sensorId) {
        List<ScenarioCondition> conditionsToRemove = scenario.getScenarioConditions().stream()
                .filter(sc -> sc.getSensor().getId().equals(sensorId))
                .toList();
        scenario.getScenarioConditions().removeAll(conditionsToRemove);
        List<ScenarioAction> actionsToRemove = scenario.getScenarioActions().stream()
                .filter(sa -> sa.getSensor().getId().equals(sensorId))
                .toList();
        scenario.getScenarioActions().removeAll(actionsToRemove);
        scenarioRepository.save(scenario);
    }
}
