package ru.yandex.practicum.telemetry.collector.service.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventProtoMapper;

@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {
    private final HubEventService hubEventService;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto hubEventProto) {
        hubEventService.handleHubEvent(HubEventProtoMapper.toHubEvent(hubEventProto));
    }
}
