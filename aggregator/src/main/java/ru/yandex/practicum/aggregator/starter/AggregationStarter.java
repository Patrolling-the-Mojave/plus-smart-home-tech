package ru.yandex.practicum.aggregator.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AggregationStarter {
    private final KafkaConsumer<String, SensorEventAvro> sensorEventConsumer;
    private final KafkaProducer<String, SensorsSnapshotAvro> sensorsSnapshotProducer;
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();
    private static final Duration POLL_DURATION = Duration.ofMillis(1000);

    @Value("${spring.kafka.topics.telemetry.sensors.v1}")
    private String sensorsTopic;

    @Value("${spring.kafka.topics.telemetry.snapshots.v1}")
    private String snapshotTopic;

    public void start() {
        try {
            sensorEventConsumer.subscribe(List.of(sensorsTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(sensorEventConsumer::wakeup));
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = sensorEventConsumer.poll(POLL_DURATION);
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    Optional<SensorsSnapshotAvro> snapshot = updateState(record.value());
                    snapshot.ifPresent(this::sendSnapshot);
                }
                sensorEventConsumer.commitSync();
            }
        } catch (WakeupException exception) {

        } catch (Exception exception) {
            log.error("произошла ошибка при обработке записей", exception);
        } finally {
            sensorsSnapshotProducer.flush();
            sensorEventConsumer.commitSync();
            sensorEventConsumer.close();
            sensorsSnapshotProducer.close();
        }
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro sensorEventAvro) {
        String hubId = sensorEventAvro.getHubId();
        SensorsSnapshotAvro snapshotAvro = snapshots.get(hubId);
        if (snapshotAvro != null) {
            SensorStateAvro oldState = snapshotAvro.getSensorsState().get(sensorEventAvro.getId());
            if (shouldSkipUpdate(oldState, sensorEventAvro.getTimestamp(), sensorEventAvro.getPayload())) {
                return Optional.empty();
            }
        }
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setData(sensorEventAvro.getPayload())
                .setTimestamp(sensorEventAvro.getTimestamp())
                .build();
        if (snapshotAvro == null) {
            snapshotAvro = SensorsSnapshotAvro.newBuilder()
                    .setHubId(hubId)
                    .setSensorsState(new HashMap<>())
                    .setTimestamp(sensorEventAvro.getTimestamp())
                    .build();
        }
        snapshotAvro.getSensorsState().put(hubId, newState);
        snapshots.put(sensorEventAvro.getId(), snapshotAvro);

        return Optional.of(snapshotAvro);
    }

    private boolean shouldSkipUpdate(SensorStateAvro oldState, Instant newTimestamp, Object newPayload) {
        if (oldState == null) {
            return false;
        }
        if (oldState.getTimestamp().isAfter(newTimestamp)) {
            return true;
        }
        Object oldPayload = oldState.getData();
        if (oldPayload == null && newPayload == null) {
            return true;
        }
        if (oldPayload == null || newPayload == null) {
            return false;
        }
        return oldPayload.equals(newPayload);
    }


    private void sendSnapshot(SensorsSnapshotAvro snapshotAvro) {
        String key = snapshotAvro.getHubId();
        ProducerRecord<String, SensorsSnapshotAvro> producerRecord = new ProducerRecord<>(snapshotTopic, key, snapshotAvro);
        sensorsSnapshotProducer.send(producerRecord);

    }
}
