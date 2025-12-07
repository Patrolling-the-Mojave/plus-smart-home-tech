package ru.yandex.practicum.analyzer.grpcservice;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Service
@Slf4j
public class HubRouterService {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterService(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendDeviceActionRequest(Scenario scenario) {
        for (ScenarioAction scenarioAction : scenario.getScenarioActions()) {
            DeviceActionProto deviceAction = toDeviceAction(scenarioAction.getAction(), scenarioAction.getSensor().getId());
            sendDeviceAction(scenario.getHubId(), deviceAction, scenario.getName());
        }
    }


    private void sendDeviceAction(String hubId, DeviceActionProto deviceAction, String scenarioName) {
        try {
            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setAction(deviceAction)
                    .setHubId(hubId)
                    .setScenarioName(scenarioName)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(System.currentTimeMillis() / 1000)
                            .setNanos((int) ((System.currentTimeMillis() % 1000) * 1_000_000))
                            .build())
                    .build();
            hubRouterClient.handleDeviceAction(request);

            log.info("Отправлено действие сценария '{}' для хаба {}}",
                    scenarioName, hubId);
        } catch (Exception exception) {
            log.error("Ошибка отправки команды в хаб {}: {}", hubId, exception.getMessage(), exception);
        }

    }

    private DeviceActionProto toDeviceAction(Action action, String sensorId) {
        return DeviceActionProto.newBuilder()
                .setType(ActionTypeProto.valueOf(action.getType().toString()))
                .setValue(action.getValue())
                .setSensorId(sensorId)
                .build();
    }

}
