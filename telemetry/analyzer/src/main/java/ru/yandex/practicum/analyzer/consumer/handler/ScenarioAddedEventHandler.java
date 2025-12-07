package ru.yandex.practicum.analyzer.consumer.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.consumer.handler.mapper.ScenarioMapper;
import ru.yandex.practicum.analyzer.exception.AnalyzerException;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioMapper scenarioMapper;


    @Override
    public HandlerType getType() {
        return HandlerType.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {
        try {
            ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) hubEventAvro.getPayload();
            String hubId = hubEventAvro.getHubId();
            String scenarioName = scenarioAddedEvent.getName();
            Optional<Scenario> existingScenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName);
            if (existingScenario.isPresent()) {
                log.info("Сценарий '{}' уже существует для хаба {}", scenarioName, hubId);
                scenarioRepository.delete(existingScenario.get());
            }
            scenarioRepository.save(scenarioMapper.toScenario(hubId, scenarioAddedEvent));
        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации при добавлении сценария: {}", e.getMessage());
            throw new AnalyzerException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка при добавлении сценария: {}", e.getMessage(), e);
            throw new AnalyzerException("Ошибка обработки сценария", e);
        }
    }
}
