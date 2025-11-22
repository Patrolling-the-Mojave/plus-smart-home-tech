package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.sensor.*;

import java.time.ZoneOffset;

public class SensorEventMapper {

    public static SensorEventAvro toAvro(SensorEvent sensorEvent) {
        Object payload = createPayload(sensorEvent);

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp().toInstant(ZoneOffset.UTC))
                .setPayload(payload)
                .build();
    }

    private static Object createPayload(SensorEvent sensorEvent) {
        return switch (sensorEvent.getType()) {
            case CLIMATE_SENSOR_EVENT -> mapClimateSensor((ClimateSensorEvent) sensorEvent);
            case LIGHT_SENSOR_EVENT -> mapLightSensor((LightSensorEvent) sensorEvent);
            case MOTION_SENSOR_EVENT -> mapMotionSensor((MotionSensorEvent) sensorEvent);
            case SWITCH_SENSOR_EVENT -> mapSwitchSensor((SwitchSensorEvent) sensorEvent);
            case TEMPERATURE_SENSOR_EVENT -> mapTemperatureSensor((TemperatureSensorEvent) sensorEvent);
        };
    }

    private static ClimateSensorAvro mapClimateSensor(ClimateSensorEvent climateSensorEvent) {
        return ClimateSensorAvro.newBuilder()
                .setCo2Level(climateSensorEvent.getCo2Level())
                .setHumidity(climateSensorEvent.getHumidity())
                .setTemperatureC(climateSensorEvent.getTemperatureC())
                .build();
    }

    private static LightSensorAvro mapLightSensor(LightSensorEvent lightSensorEvent) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }

    private static MotionSensorAvro mapMotionSensor(MotionSensorEvent motionSensorEvent) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensorEvent.getLinkQuality())
                .setMotion(motionSensorEvent.isMotion())
                .setVoltage(motionSensorEvent.getVoltage())
                .build();
    }

    private static SwitchSensorAvro mapSwitchSensor(SwitchSensorEvent switchSensorEvent) {
        return SwitchSensorAvro.newBuilder()
                .setState(switchSensorEvent.getState())
                .build();
    }

    private static TemperatureSensorAvro mapTemperatureSensor(TemperatureSensorEvent temperatureSensorEvent) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                .build();
    }
}
