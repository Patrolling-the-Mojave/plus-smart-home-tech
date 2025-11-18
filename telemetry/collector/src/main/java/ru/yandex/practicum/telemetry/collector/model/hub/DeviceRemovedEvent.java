package ru.yandex.practicum.telemetry.collector.model.hub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceRemovedEvent extends HubEvent {
    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
