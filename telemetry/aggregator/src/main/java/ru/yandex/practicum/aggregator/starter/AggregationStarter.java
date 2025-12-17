package ru.yandex.practicum.aggregator.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
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
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AggregationStarter {
    private final KafkaConsumer<String, SensorEventAvro> sensorEventConsumer;
    private final KafkaProducer<String,SpecificRecordBase> sensorsSnapshotProducer;
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
            try{
                sensorsSnapshotProducer.flush();
                sensorEventConsumer.commitSync();
            } finally {
                sensorEventConsumer.close();
                sensorsSnapshotProducer.close();
            }
        }
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();
        Instant eventTimestamp = event.getTimestamp();
        Object eventPayload = event.getPayload();

        SensorsSnapshotAvro snapshot = snapshots.get(hubId);
        if (snapshot == null) {
            snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(hubId)
                    .setSensorsState(new HashMap<>())
                    .setTimestamp(eventTimestamp)
                    .build();
            snapshots.put(hubId, snapshot);
        }
        SensorStateAvro oldState = snapshot.getSensorsState().get(sensorId);

        if (oldState != null) {
            if (oldState.getTimestamp().isAfter(eventTimestamp)) {
                return Optional.empty();
            }
            if (Objects.equals(oldState.getData(), eventPayload)) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setData(eventPayload)
                .setTimestamp(eventTimestamp)
                .build();

        snapshot.getSensorsState().put(sensorId, newState);
        snapshot.setTimestamp(eventTimestamp);

        return Optional.of(snapshot);
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshotAvro) {
        String key = snapshotAvro.getHubId();
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(snapshotTopic, key, snapshotAvro);
        sensorsSnapshotProducer.send(producerRecord);
    }
}
