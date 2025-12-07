package ru.yandex.practicum.analyzer.consumer.handler.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.*;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScenarioMapper {
    private final SensorRepository sensorRepository;

    public Scenario toScenario(String hubId, ScenarioAddedEventAvro event) {
        Scenario scenario = Scenario.builder()
                .hubId(hubId)
                .name(event.getName())
                .build();

        List<ScenarioCondition> scenarioConditions = event.getConditions().stream()
                .map(conditionAvro -> toScenarioCondition(conditionAvro, scenario))
                .collect(Collectors.toList());

        List<ScenarioAction> scenarioActions = event.getActions().stream()
                .map(actionAvro -> toScenarioAction(actionAvro, scenario))
                .collect(Collectors.toList());

        scenario.setScenarioConditions(scenarioConditions);
        scenario.setScenarioActions(scenarioActions);
        return scenario;
    }

    private ScenarioCondition toScenarioCondition(ScenarioConditionAvro conditionAvro, Scenario scenario) {
        Sensor sensor = sensorRepository.findById(conditionAvro.getSensorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Сенсор не найден: " + conditionAvro.getSensorId()));

        if (!sensor.getHubId().equals(scenario.getHubId())) {
            throw new IllegalArgumentException(
                    String.format("Сенсор %s принадлежит хабу %s, а не %s",
                            sensor.getId(), sensor.getHubId(), scenario.getHubId()));
        }

        Condition condition = Condition.builder()
                .type(ConditionType.valueOf(conditionAvro.getType().name()))
                .operation(ConditionOperation.valueOf(conditionAvro.getOperation().name()))
                .value(extractConditionValue(conditionAvro.getValue()))
                .build();

        return ScenarioCondition.builder()
                .scenario(scenario)
                .condition(condition)
                .sensor(sensor)
                .build();
    }

    private ScenarioAction toScenarioAction(DeviceActionAvro actionAvro, Scenario scenario) {
        Sensor sensor = sensorRepository.findById(actionAvro.getSensorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Устройство не найдено: " + actionAvro.getSensorId()));

        if (!sensor.getHubId().equals(scenario.getHubId())) {
            throw new IllegalArgumentException(
                    String.format("Устройство %s принадлежит хабу %s, а не %s",
                            sensor.getId(), sensor.getHubId(), scenario.getHubId()));
        }

        Action action = Action.builder()
                .type(ActionType.valueOf(actionAvro.getType().name()))
                .value(extractActionValue(actionAvro.getValue()))
                .build();

        return ScenarioAction.builder()
                .scenario(scenario)
                .action(action)
                .sensor(sensor)
                .build();
    }

    private Integer extractConditionValue(Object value) {
        if (value == null) return null;

        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? 1 : 0;
        }
        return null;
    }

    private Integer extractActionValue(Object value) {
        return (value instanceof Integer) ? (Integer) value : null;
    }
}


