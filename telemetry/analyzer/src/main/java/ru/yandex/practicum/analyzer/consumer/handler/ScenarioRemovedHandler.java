package ru.yandex.practicum.analyzer.consumer.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.exception.AnalyzerException;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;

    @Override
    public HandlerType getType() {
        return HandlerType.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {
        try {
            ScenarioRemovedEventAvro scenarioRemovedEvent = (ScenarioRemovedEventAvro) hubEventAvro.getPayload();
            String hubId = hubEventAvro.getHubId();
            String scenarioName = scenarioRemovedEvent.getName();
            Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName);
            if (scenario.isEmpty()) {
                log.warn("Сценарий '{}' не найден для хаба {}", scenarioName, hubId);
                return;
            }
            scenarioRepository.delete(scenario.get());
            log.info("Сценарий '{}' успешно удален", scenarioName);
        } catch (Exception e) {
            log.error("Ошибка при удалении сценария: {}", e.getMessage(), e);
            throw new AnalyzerException("Ошибка удаления сценария", e);
        }
    }
}
