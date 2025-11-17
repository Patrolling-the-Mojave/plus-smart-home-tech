package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.hub.*;

import java.time.ZoneOffset;

public class HubEventMapper {

    public static HubEventAvro toAvro(HubEvent hubEvent){
        Object payload = createPayload(hubEvent);
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp().toInstant(ZoneOffset.UTC))
                .setPayload(payload)
                .build();
    }

    private static Object createPayload(HubEvent hubEvent) {
        return switch (hubEvent.getType()) {
            case SCENARIO_ADDED -> mapScenarioAdded((ScenarioAddedEvent) hubEvent);
            case DEVICE_REMOVED -> mapDeviceRemoved((DeviceRemovedEvent) hubEvent);
            case SCENARIO_REMOVED -> mapScenarioRemoved((ScenarioRemovedEvent) hubEvent);
            case DEVICE_ADDED -> mapDeviceAdded((DeviceAddedEvent) hubEvent);
        };
    }


    private static ScenarioConditionAvro mapScenarioCondition(ScenarioCondition scenarioCondition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(Integer.parseInt(scenarioCondition.getSensorId()))
                .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name()))
                .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().name()))
                .setValue(scenarioCondition.getValue())
                .build();
    }

    private static DeviceActionAvro mapDeviceAction(DeviceAction deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(Integer.parseInt(deviceAction.getSensorId()))
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                .setValue(deviceAction.getValue())
                .build();
    }

    private static DeviceAddedEventAvro mapDeviceAdded(DeviceAddedEvent addedEvent) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(addedEvent.getId())
                .setType(DeviceTypeAvro.valueOf(addedEvent.getType().name()))
                .build();
    }

    private static DeviceRemovedEventAvro mapDeviceRemoved(DeviceRemovedEvent removedEvent) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(removedEvent.getId())
                .build();
    }

    private static ScenarioAddedEventAvro mapScenarioAdded(ScenarioAddedEvent scenarioAddedEvent) {
        return ScenarioAddedEventAvro.newBuilder()
                .setActions(scenarioAddedEvent.getActions().stream().map(HubEventMapper::mapDeviceAction).toList())
                .setConditions(scenarioAddedEvent.getConditions().stream().map(HubEventMapper::mapScenarioCondition).toList())
                .setName(scenarioAddedEvent.getName())
                .build();
    }

    private static ScenarioRemovedEventAvro mapScenarioRemoved(ScenarioRemovedEvent scenarioRemovedEvent) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEvent.getName())
                .build();
    }

}
