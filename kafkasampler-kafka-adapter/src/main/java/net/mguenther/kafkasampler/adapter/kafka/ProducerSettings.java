package net.mguenther.kafkasampler.adapter.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Properties;

/**
 * Represents the settings for a {@code Producer}.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
@ToString
public class ProducerSettings<InputType, OutputType> {

    private static final String DEFAULT_ACKS = "all";

    private static final int DEFAULT_NUMBER_OF_RETRIES = 0;

    private static final int DEFAULT_BATCH_SIZE = 16384;

    private static final int DEFAULT_LINGER_IN_MILLIS = 0;

    private static final String DEFAULT_BOOTSTRAP_SERVER = "localhost:9092";

    private static final String DEFAULT_KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";

    public static class ProducerSettingsBuilder<InputType, OutputType> {

        private final Encoder<InputType, OutputType> encoder;

        private String bootstrapServer = DEFAULT_BOOTSTRAP_SERVER;

        private String acks = DEFAULT_ACKS;

        private int numberOfRetries = DEFAULT_NUMBER_OF_RETRIES;

        private int batchSize = DEFAULT_BATCH_SIZE;

        private int lingerInMillis = DEFAULT_LINGER_IN_MILLIS;

        public ProducerSettingsBuilder(final Encoder<InputType, OutputType> encoder) {
            this.encoder = encoder;
        }

        public ProducerSettingsBuilder usingBootstrapServer(final String bootstrapServer) {
            this.bootstrapServer = bootstrapServer;
            return this;
        }

        public ProducerSettings<InputType, OutputType> build() {

            final Properties properties = new Properties();
            properties.put("bootstrap.servers", bootstrapServer);
            properties.put("acks", acks);
            properties.put("retries", numberOfRetries);
            properties.put("batch.size", batchSize);
            properties.put("linger.ms", lingerInMillis);
            properties.put("key.serializer", DEFAULT_KEY_SERIALIZER);
            properties.put("value.serializer", encoder.underlyingKafkaSerializer());

            return new ProducerSettings<>(encoder, properties);
        }
    }

    private final Encoder<InputType, OutputType> encoder;

    private final Properties properties;

    public static <InputType, OutputType> ProducerSettingsBuilder<InputType, OutputType> builder(final Encoder<InputType, OutputType> encoder) {
        return new ProducerSettingsBuilder<>(encoder);
    }

    public static <InputType, OutputType> ProducerSettings<InputType, OutputType> usingDefaults(final Encoder<InputType, OutputType> encoder) {
        return new ProducerSettingsBuilder<>(encoder).build();
    }
}
