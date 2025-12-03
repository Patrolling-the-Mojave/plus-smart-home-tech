package ru.yandex.practicum.aggregator.consumer.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

public abstract class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final DecoderFactory decoderFactory;
    private final Schema schema;
    private final DatumReader<T> datumReader;

    public BaseAvroDeserializer(Schema schema) {
        this.decoderFactory = DecoderFactory.get();
        this.schema = schema;
        this.datumReader = new SpecificDatumReader<>();
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.schema = schema;
        this.decoderFactory = decoderFactory;
        this.datumReader = new SpecificDatumReader<>();
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        try {
            if (bytes != null) {
                BinaryDecoder decoder = decoderFactory.binaryDecoder(bytes, null);
                return this.datumReader.read(null, decoder);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
