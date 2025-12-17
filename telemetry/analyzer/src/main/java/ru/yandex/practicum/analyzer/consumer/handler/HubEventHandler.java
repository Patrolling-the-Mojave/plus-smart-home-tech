package ru.yandex.practicum.analyzer.consumer.handler;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventHandler {
    void handle(HubEventAvro hubEventAvro);

    HandlerType getType();
}
