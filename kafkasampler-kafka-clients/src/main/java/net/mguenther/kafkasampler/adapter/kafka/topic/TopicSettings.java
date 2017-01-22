package net.mguenther.kafkasampler.adapter.kafka.topic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Properties;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
@ToString
public class TopicSettings {

    private static final int DEFAULT_NUMBER_OF_PARTITIONS = 1;

    private static final int DEFAULT_NUMBER_OF_REPLICAS = 1;

    public static class TopicSettingsBuilder {

        private final String topic;
        private int numberOfPartitions = DEFAULT_NUMBER_OF_PARTITIONS;
        private int numberOfReplicas = DEFAULT_NUMBER_OF_REPLICAS;

        public TopicSettingsBuilder(final String topic) {
            this.topic = topic;
        }

        public TopicSettingsBuilder withNumberOfPartitions(final int numberOfPartitions) {
            this.numberOfPartitions = numberOfPartitions;
            return this;
        }

        public TopicSettingsBuilder withNumberOfReplicas(final int numberOfReplicas) {
            this.numberOfReplicas = numberOfReplicas;
            return this;
        }

        public TopicSettings build() {
            final Properties properties = new Properties();
            properties.setProperty("cleanup.policy", "delete");
            properties.setProperty("delete.retention.ms", "86400000");
            // Please note that the minimum value for this configuration parameter must be 1,
            // since each committed message in a Kafka log has to be managed by at least one
            // broker
            properties.setProperty("min.insync.replicas", "1");
            return new TopicSettings(topic, numberOfPartitions, numberOfReplicas, properties);
        }
    }

    private final String topic;

    private final int numberOfPartitions;

    private final int numberOfReplicas;

    private final Properties properties;

    public static TopicSettingsBuilder builder(final String topic) {
        return new TopicSettingsBuilder(topic);
    }

    public static TopicSettings useDefaults(final String topic) {
        return new TopicSettingsBuilder(topic).build();
    }
}
