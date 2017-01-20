package net.mguenther.kafkasampler.adapter.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@code Consumer} is a thread-safe wrapper around an underlying {@code KafkaConsumer}. Its
 * main concern is consuming messages from the subscribed topics and dispatching them to a
 * {@code Processor} that handles application-level messages of type {@code OutputType}.
 * Processing messages is decoupled from the {@code Consumer} asynchronously.
 *
 * This {@code Consumer} should be terminated by calling its {@link Consumer#stop()} method.
 *
 * This {@code Consumer} is not able to manage consumer offsets explicitly.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class Consumer<InputType, OutputType> implements Runnable {

    private final String consumerId;

    private final Decoder<InputType, OutputType> decoder;

    private final Processor<OutputType> processor;

    private final List<String> topics;

    private final ConsumerSettings<InputType, OutputType> consumerSettings;

    private final int readTimeoutInMillis;

    private volatile boolean running = true;

    private KafkaConsumer<String, InputType> underlyingConsumer;

    public Consumer(final String consumerId,
                    final List<String> topics,
                    final ConsumerSettings<InputType, OutputType> consumerSettings) {
        this.consumerId = consumerId;
        this.topics = topics;
        this.decoder = consumerSettings.getDecoder();
        this.processor = consumerSettings.getProcessor();
        this.consumerSettings = consumerSettings;
        this.readTimeoutInMillis = consumerSettings.getReadTimeoutInMillis();
    }

    @Override
    public void run() {
        initializeConsumer();
        while (isRunning()) {
            pollLog();
        }
        shutdownConsumer();
    }

    private void initializeConsumer() {
        log.info("[{}] Preparing to initialize consumer.", consumerId);
        underlyingConsumer = new KafkaConsumer<String, InputType>(consumerSettings.getProperties());
        underlyingConsumer.subscribe(topics);
        log.info("[{}] Subscribed consumer to the following topics: {}", consumerId, topics.stream().collect(Collectors.joining()));
    }

    private void shutdownConsumer() {
        try {
            log.debug("[{}] Shutting down consumer.", consumerId);
            log.debug("[{}] Closing underlying KafkaConsumer. This may take a while depending on the state of the consumer.", consumerId);
            underlyingConsumer.close();
            log.info("[{}] Consumer has been successfully shut down.", consumerId);
        } catch (Exception e) {
            log.warn("[{}] Caught an exception while trying to close the underlying KafkaConsumer.", consumerId, e);
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void pollLog() {
        try {
            final ConsumerRecords<String, InputType> records = underlyingConsumer.poll(readTimeoutInMillis);
            log.trace("[{}] Read {} records.", consumerId, records.count());
            records.forEach(this::consume);
        } catch (KafkaException e) {
            log.warn("[{}] Unable to recover from caught KafkaException. Terminating this consumer.", consumerId);
            stop();
        }
    }

    private void consume(final ConsumerRecord<String, InputType> record) {
        log.debug("[{}] Consumed record of topic={} at partition={} and offset={} with key={} and value={}.", consumerId, record.topic(), record.partition(), record.offset(), record.key(), record.value());
        decoder.decode(record.value()).ifPresent(processor::process);
    }

    public void stop() {
        log.info("[{}] Received stop signal.");
        running = false;
    }
}
