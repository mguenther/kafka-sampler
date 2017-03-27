package net.mguenther.kafkasampler.adapter.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * {@code Producer} is a simple wrapper around an underlying {@link KafkaProducer} that is
 * ensures type-compatibility between application-level messages and messages that are
 * serialized to Kafka using a dedicated {@code Encoder} for types {@code InputType} and
 * {@code OutputType}.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class Producer<InputType, OutputType> {

    private final String producerId;

    private final Encoder<InputType, OutputType> encoder;

    private final KafkaProducer<String, OutputType> underlyingProducer;

    public Producer(final String producerId, final ProducerSettings<InputType, OutputType> producerSettings) {
        this.producerId = producerId;
        this.encoder = producerSettings.getEncoder();
        this.underlyingProducer = new KafkaProducer<>(producerSettings.getProperties());
        log.info("[{}] Initialized producer with settings {}.", producerId, producerSettings);
    }

    public void log(final String topic, final InputType message) {
        encoder.encode(message).ifPresent(encodedMessage -> {
            final ProducerRecord<String, OutputType> record = new ProducerRecord<>(topic, encodedMessage);
            underlyingProducer.send(record);
            log.debug("[{}] Submitted message '{}' to topic '{}'.", producerId, message, topic);
        });
    }

    public void log(final String topic, final String key, final InputType message) {
        encoder.encode(message).ifPresent(encodedMessage -> {
            final ProducerRecord<String, OutputType> record = new ProducerRecord<>(topic, key, encodedMessage);
            underlyingProducer.send(record);
            log.debug("[{}] Submitted keyed message '{}' with key '{}' to topic '{}'.", producerId, message, key, topic);
        });
    }
}
