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
        SensorsSnapshotAvro snapshotAvro;
        if (snapshots.containsKey(hubId)) {
            snapshotAvro = snapshots.get(hubId);
            if (snapshotAvro.getSensorsState().containsKey(sensorEventAvro.getId())) {
                SensorStateAvro oldState = snapshotAvro.getSensorsState().get(sensorEventAvro.getId());
                if (oldState.getTimestamp().isAfter(sensorEventAvro.getTimestamp()) || oldState.getData().equals(sensorEventAvro.getPayload())) {
                    return Optional.empty();
                }
            }
        } else {
            snapshotAvro = new SensorsSnapshotAvro();
            snapshotAvro.setHubId(hubId);
            snapshots.put(hubId, snapshotAvro);
        }

        SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(sensorEventAvro.getTimestamp())
                .setData(sensorEventAvro)
                .build();
        snapshotAvro.setTimestamp(sensorStateAvro.getTimestamp());
        snapshotAvro.getSensorsState().put(sensorEventAvro.getId(), sensorStateAvro);

        return Optional.of(snapshotAvro);
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshotAvro) {
        String key = snapshotAvro.getHubId();
        ProducerRecord<String, SensorsSnapshotAvro> producerRecord = new ProducerRecord<>(snapshotTopic, key, snapshotAvro);
        sensorsSnapshotProducer.send(producerRecord);

    }
}
