package ru.yandex.practicum.analyzer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.consumer.handler.HandlerType;
import ru.yandex.practicum.analyzer.consumer.handler.HubEventHandler;
import ru.yandex.practicum.analyzer.exception.AnalyzerException;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, HubEventAvro> hubEventKafkaConsumer;
    private final Duration POLL_DURATION = Duration.ofMillis(5000);
    private final Map<HandlerType, HubEventHandler> handlers;

    @Value("${spring.kafka.topics.telemetry.hubs.v1}")
    private String hubEventsTopic;

    public HubEventProcessor(KafkaConsumer<String, HubEventAvro> hubEventKafkaConsumer, List<HubEventHandler> handlers) {
        this.hubEventKafkaConsumer = hubEventKafkaConsumer;
        this.handlers = handlers.stream().collect(Collectors.toMap(HubEventHandler::getType, Function.identity()));
    }

    @Override
    public void run() {
        try {
            hubEventKafkaConsumer.subscribe(List.of(hubEventsTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(hubEventKafkaConsumer::wakeup));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = hubEventKafkaConsumer.poll(POLL_DURATION);
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    processHubEvent(record.value());
                }
                hubEventKafkaConsumer.commitSync();
            }
        } catch (WakeupException exception) {

        } catch (Exception exception) {
            log.warn("Произошла ошибка при обработке данных из топика{}", hubEventsTopic);
            throw new AnalyzerException(exception.getMessage(), exception);
        } finally {
            try {
                hubEventKafkaConsumer.commitSync();
            } finally {
                hubEventKafkaConsumer.close();
            }
        }
    }

    private void processHubEvent(HubEventAvro hubEventAvro) {
        Object payload = hubEventAvro.getPayload();
        if (payload instanceof ScenarioAddedEventAvro) {
            handlers.get(HandlerType.SCENARIO_ADDED).handle(hubEventAvro);
        } else if (payload instanceof ScenarioRemovedEventAvro) {
            handlers.get(HandlerType.SCENARIO_REMOVED).handle(hubEventAvro);
        } else if (payload instanceof DeviceAddedEventAvro) {
            handlers.get(HandlerType.SENSOR_ADDED).handle(hubEventAvro);
        } else if (payload instanceof DeviceRemovedEventAvro) {
            handlers.get(HandlerType.SENSOR_REMOVED);
        }
    }
}
