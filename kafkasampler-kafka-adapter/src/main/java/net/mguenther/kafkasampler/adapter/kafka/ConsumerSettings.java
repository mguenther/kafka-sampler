package net.mguenther.kafkasampler.adapter.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * Represents the settings for a {@code Consumer}.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
@ToString
@Slf4j
public class ConsumerSettings<InputType, OutputType> {

    private static final boolean DEFAULT_ENABLE_AUTO_COMMIT = true;

    private static final int DEFAULT_AUTO_COMMIT_INTERVAL_IN_MS = 1_000;

    private static final int DEFAULT_SESSION_TIMEOUT_IN_MS = 30_000;

    private static final int DEFAULT_READ_TIMEOUT_IN_MS = 10_000;

    private static final String DEFAULT_BOOTSTRAP_SERVER = "localhost:9092";

    private static final String DEFAULT_KEY_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";

    private static final String DEFAULT_AUTO_OFFSET_RESET = "latest";

    public static class ConsumerSettingsBuilder<InputType, OutputType> {

        private final String consumerGroupId;

        private final Decoder<InputType, OutputType> decoder;

        private final Processor<OutputType> processor;

        private int readTimeoutInMillis = DEFAULT_READ_TIMEOUT_IN_MS;

        private String bootstrapServer = DEFAULT_BOOTSTRAP_SERVER;

        private boolean enableAutoCommit = DEFAULT_ENABLE_AUTO_COMMIT;

        private int autoCommitIntervalInMillis = DEFAULT_AUTO_COMMIT_INTERVAL_IN_MS;

        private int sessionTimeoutInMillis = DEFAULT_SESSION_TIMEOUT_IN_MS;

        private String autoOffsetReset = DEFAULT_AUTO_OFFSET_RESET;

        public ConsumerSettingsBuilder(final String consumerGroupId,
                                       final Decoder<InputType, OutputType> decoder,
                                       final Processor<OutputType> processor) {
            this.consumerGroupId = consumerGroupId;
            this.decoder = decoder;
            this.processor = processor;
        }

        public ConsumerSettingsBuilder usingBootstrapServer(final String bootstrapServer) {
            this.bootstrapServer = bootstrapServer;
            return this;
        }

        public ConsumerSettings<InputType, OutputType> build() {

            if (autoCommitIntervalInMillis < 1_000) {
                log.info("Provided parameter 'auto.commit.interval.ms={}' is invalid. Setting it to {} ms.", autoCommitIntervalInMillis, DEFAULT_AUTO_COMMIT_INTERVAL_IN_MS);
                autoCommitIntervalInMillis = DEFAULT_AUTO_COMMIT_INTERVAL_IN_MS;
            }

            if (sessionTimeoutInMillis < autoCommitIntervalInMillis) {
                log.info("Provided parameter 'session.timeout.ms={}' is invalid. Setting it to {} ms.", sessionTimeoutInMillis, autoCommitIntervalInMillis * 4);
                sessionTimeoutInMillis = autoCommitIntervalInMillis * 4;
            }

            final Properties properties = new Properties();
            properties.put("bootstrap.servers", bootstrapServer);
            properties.put("enable.auto.commit", enableAutoCommit);
            properties.put("auto.commit.interval.ms", autoCommitIntervalInMillis);
            properties.put("session.timeout.ms", sessionTimeoutInMillis);
            properties.put("key.deserializer", DEFAULT_KEY_DESERIALIZER);
            properties.put("value.deserializer", decoder.underlyingKafkaDeserializer());
            properties.put("auto.offset.reset", autoOffsetReset);

            return new ConsumerSettings<>(decoder, processor, consumerGroupId, readTimeoutInMillis, properties);
        }
    }

    private final Decoder<InputType, OutputType> decoder;

    private final Processor<OutputType> processor;

    private final String consumerGroupId;

    private final int readTimeoutInMillis;

    private final Properties properties;
}
