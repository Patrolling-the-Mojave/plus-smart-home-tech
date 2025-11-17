package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScenarioRemovedEvent extends HubEvent {
    @NotNull
    private String name;

    @Override
    protected HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}

