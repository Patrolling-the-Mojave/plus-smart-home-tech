package ru.yandex.practicum.aggregator.producer.serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.io.ByteArrayOutputStream;

public class SensorSnapshotSerializer implements Serializer<SensorsSnapshotAvro> {

    @Override
    public byte[] serialize(String s, SensorsSnapshotAvro sensorsSnapshotAvro) {
        if (sensorsSnapshotAvro == null) {
            return null;
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            DatumWriter<SensorsSnapshotAvro> writer = new SpecificDatumWriter<>(sensorsSnapshotAvro.getSchema());
            writer.write(sensorsSnapshotAvro, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new SerializationException("не удалось сериализовать данные " + sensorsSnapshotAvro.getSchema().getFullName());
        }
    }
}
