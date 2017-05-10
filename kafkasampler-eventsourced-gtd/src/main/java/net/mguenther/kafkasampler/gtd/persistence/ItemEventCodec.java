package net.mguenther.kafkasampler.gtd.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafkasampler.adapter.kafka.Codec;
import net.mguenther.kafkasampler.gtd.domain.events.ItemEvent;
import net.mguenther.kafkasampler.gtd.persistence.serialization.Event;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Slf4j
public class ItemEventCodec implements Codec<ItemEvent, byte[]> {

    private final AvroDomainConverter converter;
    private final DatumWriter<Event> eventWriter = new SpecificDatumWriter<>(Event.class);
    private final DatumReader<Event> eventReader = new SpecificDatumReader<>(Event.class);

    @Override
    public Optional<ItemEvent> decode(final byte[] input) {

        final Decoder decoder = DecoderFactory.get().binaryDecoder(input, null);
        try {
            final ItemEvent event = converter.to(eventReader.read(null, decoder));
            return Optional.of(event);
        } catch (Exception e) {
            log.warn("Unable to decode payload '{}' to instance of ItemEvent.", input, e);
            return Optional.empty();
        }
    }

    @Override
    public Class<? extends Deserializer<byte[]>> underlyingKafkaDeserializer() {
        return ByteArrayDeserializer.class;
    }

    @Override
    public Optional<byte[]> encode(final ItemEvent input) {
        final Event event = converter.from(input);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            eventWriter.write(event, encoder);
            encoder.flush();
            return Optional.of(out.toByteArray());
        } catch (Exception e) {
            log.warn("Unable to encode event '{}' to Avro-encoded record.", input, e);
            return Optional.empty();
        }
    }

    @Override
    public Class<? extends Serializer<byte[]>> underlyingKafkaSerializer() {
        return ByteArraySerializer.class;
    }
}
