package ru.yandex.practicum.analyzer.consumer.deserializer;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class HubEventDeserializer implements Deserializer<HubEventAvro> {
    private final DatumReader<HubEventAvro> reader = new SpecificDatumReader<>(HubEventAvro.getClassSchema());

    @Override
    public HubEventAvro deserialize(String s, byte[] bytes) {
        try {
            if (bytes != null) {
                BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
                return this.reader.read(null, decoder);
            }
            return null;
        } catch (Exception exception) {
            throw new SerializationException(
                    String.format("произошла ошибка при десериализации данных avro для топика %s. " +
                                    "схема: %s, ошибка: %s",
                            s, SensorsSnapshotAvro.getClassSchema().getFullName(), exception.getMessage())
            );
        }
    }
}
