package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.*;

import java.time.Instant;
import java.time.ZoneOffset;

public class HubEventProtoMapper {

    public static HubEvent toHubEvent(HubEventProto proto) {
        HubEvent hubEvent = switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> toDeviceAddedEvent(proto.getDeviceAdded());
            case DEVICE_REMOVED -> toDeviceRemovedEvent(proto.getDeviceRemoved());
            case SCENARIO_ADDED -> toScenarioAddedEvent(proto.getScenarioAdded());
            case SCENARIO_REMOVED -> toScenarioRemovedEvent(proto.getScenarioRemoved());
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Hub event type not set");
        };
        hubEvent.setHubId(proto.getHubId());
        hubEvent.setTimestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(),
                        proto.getTimestamp().getNanos())
                .atZone(ZoneOffset.UTC).toLocalDateTime());
        return hubEvent;
    }

    private static DeviceAddedEvent toDeviceAddedEvent(DeviceAddedEventProto deviceProto) {
        return DeviceAddedEvent.builder()
                .id(deviceProto.getId())
                .deviceType(DeviceType.valueOf(deviceProto.getType().name()))
                .build();
    }

    private static DeviceRemovedEvent toDeviceRemovedEvent(DeviceRemovedEventProto deviceProto) {
        return DeviceRemovedEvent.builder()
                .id(deviceProto.getId())
                .build();
    }

    private static ScenarioAddedEvent toScenarioAddedEvent(ScenarioAddedEventProto scenarioProto) {
        return ScenarioAddedEvent.builder()
                .name(scenarioProto.getName())
                .conditions(scenarioProto.getConditionList().stream()
                        .map(HubEventProtoMapper::toScenarioCondition)
                        .toList())
                .actions(scenarioProto.getActionList().stream()
                        .map(HubEventProtoMapper::toDeviceAction)
                        .toList())
                .build();
    }

    private static ScenarioRemovedEvent toScenarioRemovedEvent(ScenarioRemovedEventProto scenarioProto) {
        return ScenarioRemovedEvent.builder()
                .name(scenarioProto.getName())
                .build();
    }

    private static ScenarioCondition toScenarioCondition(ScenarioConditionProto proto) {
        Object value = switch (proto.getValueCase()){
            case BOOL_VALUE -> proto.getBoolValue();
            case INT_VALUE -> proto.getIntValue();
            case VALUE_NOT_SET -> null;
        };

        return ScenarioCondition.builder()
                .sensorId(proto.getSensorId())
                .type(ConditionType.valueOf(proto.getType().name()))
                .operation(ConditionOperation.valueOf(proto.getOperation().name()))
                .value(value)
                .build();
    }

    private static DeviceAction toDeviceAction(DeviceActionProto proto) {
        Integer value = proto.hasValue() ? proto.getValue() : null;

        return DeviceAction.builder()
                .sensorId(proto.getSensorId())
                .type(ActionType.valueOf(proto.getType().name()))
                .value(value)
                .build();
    }
}


