package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class EventProducer {
    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    public <T extends SpecificRecordBase> void send(T event, String topic, String key) {
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(topic, key, event);
        kafkaProducer.send(producerRecord);
    }

}
