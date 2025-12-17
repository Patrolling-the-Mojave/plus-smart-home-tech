package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.*;

import java.time.Instant;
import java.time.ZoneOffset;

public class SensorEventProtoMapper {

    public static SensorEvent toSensorEvent(SensorEventProto proto) {
        SensorEvent sensorEvent = switch (proto.getPayloadCase()) {
            case MOTION_SENSOR -> toMotionSensorEvent(proto.getMotionSensor());
            case TEMPERATURE_SENSOR -> toTemperatureSensorEvent(proto.getTemperatureSensor());
            case LIGHT_SENSOR -> toLightSensorEvent(proto.getLightSensor());
            case CLIMATE_SENSOR -> toClimateSensorEvent(proto.getClimateSensor());
            case SWITCH_SENSOR -> toSwitchSensorEvent(proto.getSwitchSensor());
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Hub event type not set");
        };
        sensorEvent.setId(proto.getId());
        sensorEvent.setTimestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos())
                .atZone(ZoneOffset.UTC).toLocalDateTime());
        sensorEvent.setHubId(proto.getHubId());
        return sensorEvent;
    }

    private static MotionSensorEvent toMotionSensorEvent(MotionSensorProto sensorProto) {
        return MotionSensorEvent.builder()
                .motion(sensorProto.getMotion())
                .linkQuality(sensorProto.getLinkQuality())
                .voltage(sensorProto.getVoltage())
                .build();
    }

    private static TemperatureSensorEvent toTemperatureSensorEvent(TemperatureSensorProto sensorEventProto) {
        return TemperatureSensorEvent.builder()
                .temperatureC(sensorEventProto.getTemperatureC())
                .temperatureF(sensorEventProto.getTemperatureF())
                .build();
    }

    private static LightSensorEvent toLightSensorEvent(LightSensorProto lightProto) {
        return LightSensorEvent.builder()
                .linkQuality(lightProto.getLinkQuality())
                .luminosity(lightProto.getLuminosity())
                .build();
    }

    private static ClimateSensorEvent toClimateSensorEvent(ClimateSensorProto climateProto) {
        return ClimateSensorEvent.builder()
                .temperatureC(climateProto.getTemperatureC())
                .humidity(climateProto.getHumidity())
                .co2Level(climateProto.getCo2Level())
                .build();
    }

    private static SwitchSensorEvent toSwitchSensorEvent(SwitchSensorProto switchProto) {
        return SwitchSensorEvent.builder()
                .state(switchProto.getState())
                .build();
    }


}
