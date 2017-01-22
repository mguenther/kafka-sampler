package net.mguenther.kafkasampler.adapter.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Optional;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class StringIdentityCodec implements Codec<String, String> {

    @Override
    public Optional<String> decode(final String input) {
        return Optional.of(input);
    }

    @Override
    public Class<? extends Deserializer<String>> underlyingKafkaDeserializer() {
        return StringDeserializer.class;
    }

    @Override
    public Optional<String> encode(final String input) {
        return Optional.of(input);
    }

    @Override
    public Class<? extends Serializer<String>> underlyingKafkaSerializer() {
        return StringSerializer.class;
    }
}
