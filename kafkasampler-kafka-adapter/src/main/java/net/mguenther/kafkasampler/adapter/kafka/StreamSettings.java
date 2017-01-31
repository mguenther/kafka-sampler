package net.mguenther.kafkasampler.adapter.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
@ToString
@Slf4j
public class StreamSettings<KeyType, ValueType> {

    private static final String DEFAULT_BOOTSTRAP_SERVER = "localhost:9092";

    private static final String DEFAULT_ZOOKEEPER_URL = "localhost:2181";

    private static final String DEFAULT_AUTO_OFFSET_RESET_CONFIG = "earliest";

    public static class StreamSettingsBuilder<KeyType, ValueType> {

        private final String applicationId;

        private final Serde<KeyType> keyCodec;

        private final Serde<ValueType> valueCodec;

        private String bootstrapServer = DEFAULT_BOOTSTRAP_SERVER;

        private String zookeeperUrl = DEFAULT_ZOOKEEPER_URL;

        private String autoOffsetResetConfig = DEFAULT_AUTO_OFFSET_RESET_CONFIG;

        public StreamSettingsBuilder(final String applicationId,
                                     final Serde<KeyType> keyCodec,
                                     final Serde<ValueType> valueCodec) {
            this.applicationId = applicationId;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        public StreamSettingsBuilder usingBootstrapServer(final String bootstrapServer) {
            this.bootstrapServer = bootstrapServer;
            return this;
        }

        public StreamSettingsBuilder usingZookeeperAt(final String zookeeperUrl) {
            this.zookeeperUrl = zookeeperUrl;
            return this;
        }

        public StreamSettings<KeyType, ValueType> build() {

            final Properties properties = new Properties();
            properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "twitter-sentiment-analysis");
            properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
            properties.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, zookeeperUrl);
            properties.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, keyCodec.getClass().getName());
            properties.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, valueCodec.getClass().getName());
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig);
            return new StreamSettings<>(applicationId, keyCodec, valueCodec, properties);
        }
    }

    private final String applicationId;
    private final Serde<KeyType> keyCodec;
    private final Serde<ValueType> valueCodec;
    private final Properties properties;

    public static <KeyType, ValueType> StreamSettingsBuilder<KeyType, ValueType> builder(final String applicationId,
                                                                                         final Serde<KeyType> keyCodec,
                                                                                         final Serde<ValueType> valueCodec) {
        return new StreamSettingsBuilder<>(applicationId, keyCodec, valueCodec);
    }

    public static StreamSettings<String, String> usingDefaults(final String applicationId) {
        return new StreamSettingsBuilder<>(applicationId, Serdes.String(), Serdes.String()).build();
    }
}
