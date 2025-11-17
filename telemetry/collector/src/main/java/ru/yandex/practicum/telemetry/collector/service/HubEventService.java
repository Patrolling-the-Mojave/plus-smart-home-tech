package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapper;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubEventService {
    private final EventProducer eventProducer;

    @Value("${spring.kafka.topics.telemetry.hubs.v1}")
    private String hubEventTopic;

    public HubEvent handleHubEvent(HubEvent hubEvent){
        eventProducer.send(HubEventMapper.toAvro(hubEvent), hubEventTopic, hubEvent.getHubId());
        log.debug("сообщение{} успешно отправлено в топик{}", hubEvent, hubEventTopic);
        return hubEvent;
    }
}
