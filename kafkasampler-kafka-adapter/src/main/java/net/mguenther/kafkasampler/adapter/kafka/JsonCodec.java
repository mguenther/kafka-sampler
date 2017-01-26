package net.mguenther.kafkasampler.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.util.Optional;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
@RequiredArgsConstructor
public class JsonCodec<T> implements Codec<T, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Class<T> objectType;

    @Override
    public Optional<T> decode(final String input) {
        try {
            return Optional.of(MAPPER.readValue(input, objectType));
        } catch (IOException e) {
            final String truncatedInput = input.length() > 20 ? input.substring(0, 20).concat("...") : input;
            log.warn("Unable to decode '{}' to instance of {}.", truncatedInput, objectType);
            return Optional.empty();
        }
    }

    @Override
    public Class<? extends Deserializer<String>> underlyingKafkaDeserializer() {
        return StringDeserializer.class;
    }

    @Override
    public Optional<String> encode(final T input) {
        try {
            return Optional.of(MAPPER.writeValueAsString(input));
        } catch (IOException e) {
            log.warn("Unable to encode {} to JSON string.", input);
            return Optional.empty();
        }
    }

    @Override
    public Class<? extends Serializer<String>> underlyingKafkaSerializer() {
        return StringSerializer.class;
    }
}
