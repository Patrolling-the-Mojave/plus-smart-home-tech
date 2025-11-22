package ru.yandex.practicum.telemetry.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.service.HubEventService;
import ru.yandex.practicum.telemetry.collector.service.SensorEventService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/events")
public class EventController {
    private final HubEventService hubEventService;
    private final SensorEventService sensorEventService;

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void handleHubEvent(@Validated @RequestBody HubEvent hubEvent) {
        hubEventService.handleHubEvent(hubEvent);
    }

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void handleSensorEvent(@Validated @RequestBody SensorEvent sensorEvent) {
        sensorEventService.handleSensorEvent(sensorEvent);
    }
}
