package ru.yandex.practicum.analyzer.consumer.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.consumer.handler.mapper.ScenarioMapper;
import ru.yandex.practicum.analyzer.exception.AnalyzerException;
import ru.yandex.practicum.analyzer.model.*;
import ru.yandex.practicum.analyzer.repository.ActionRepository;
import ru.yandex.practicum.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;


@Component
@Slf4j
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioMapper scenarioMapper;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    @Override
    public HandlerType getType() {
        return HandlerType.SCENARIO_ADDED;
    }

    @Override
    @Transactional
    public void handle(HubEventAvro hubEventAvro) {
        try {
            ScenarioAddedEventAvro event = (ScenarioAddedEventAvro) hubEventAvro.getPayload();
            String hubId = hubEventAvro.getHubId();
            String scenarioName = event.getName();

            scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                    .ifPresent(existingScenario -> {
                        existingScenario.getScenarioConditions().clear();
                        existingScenario.getScenarioActions().clear();
                        scenarioRepository.saveAndFlush(existingScenario);
                        scenarioRepository.delete(existingScenario);
                        scenarioRepository.flush();
                        log.info("Удален существующий сценарий '{}' для хаба {}", scenarioName, hubId);
                    });

            Scenario scenario = new Scenario();
            scenario.setHubId(hubId);
            scenario.setName(scenarioName);

            for (ScenarioConditionAvro condAvro : event.getConditions()) {
                Sensor sensor = getAndValidateSensor(condAvro.getSensorId(), hubId);

                Condition condition = new Condition();
                condition.setType(ConditionType.valueOf(condAvro.getType().name()));
                condition.setOperation(ConditionOperation.valueOf(condAvro.getOperation().name()));
                condition.setValue(extractValue(condAvro.getValue()));
                condition = conditionRepository.save(condition);

                ScenarioCondition sc = new ScenarioCondition();
                sc.setScenario(scenario);
                sc.setSensor(sensor);
                sc.setCondition(condition);
                scenario.addCondition(sc);
            }

            for (DeviceActionAvro actAvro : event.getActions()) {
                Sensor sensor = getAndValidateSensor(actAvro.getSensorId(), hubId);

                Action action = new Action();
                action.setType(ActionType.valueOf(actAvro.getType().name()));
                action.setValue(extractValue(actAvro.getValue()));
                action = actionRepository.save(action);

                ScenarioAction sa = new ScenarioAction();
                sa.setScenario(scenario);
                sa.setSensor(sensor);
                sa.setAction(action);
                scenario.addAction(sa);

            }
            scenarioRepository.save(scenario);

            log.info("Сценарий '{}' успешно сохранён для хаба {}", scenarioName, hubId);

        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации при добавлении сценария: {}", e.getMessage());
            throw new AnalyzerException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка при добавлении сценария", e);
            throw new AnalyzerException("Ошибка обработки сценария", e);
        }
    }

    private Sensor getAndValidateSensor(String sensorId, String expectedHubId) {
        return sensorRepository.findById(sensorId)
                .filter(sensor -> expectedHubId.equals(sensor.getHubId()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Сенсор " + sensorId + " не найден или принадлежит другому хабу"));
    }

    private Integer extractValue(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Boolean) return ((Boolean) value) ? 1 : 0;
        return null;
    }
}

