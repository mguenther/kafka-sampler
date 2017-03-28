package net.mguenther.kafkasampler.streams.joins;

import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafkasampler.adapter.kafka.Codec;
import net.mguenther.kafkasampler.adapter.kafka.Producer;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import net.mguenther.kafkasampler.adapter.kafka.StreamSettings;
import net.mguenther.kafkasampler.adapter.kafka.StringIdentityCodec;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class InnerJoinExample {

    private static final String INSTANCE_ID = UUID.randomUUID().toString().substring(0, 4);
    private static final String TOPIC_CUSTOMERS = String.format("customers-%s", INSTANCE_ID);
    private static final String TOPIC_LOCATIONS = String.format("locations-%s", INSTANCE_ID);
    private static final String PRODUCER_ID = String.format("producer-%s", INSTANCE_ID);

    public static void main(String[] args) throws Exception {

        final Codec<String, String> codec = new StringIdentityCodec();

        final Producer<String, String> producer = new Producer<>(PRODUCER_ID, ProducerSettings.usingDefaults(codec));

        producer.log(TOPIC_CUSTOMERS, "101", "Müller");
        producer.log(TOPIC_CUSTOMERS, "102", "Schmidt");
        producer.log(TOPIC_CUSTOMERS, "103", "Schneider");
        producer.log(TOPIC_CUSTOMERS, "104", "Fischer");

        producer.log(TOPIC_LOCATIONS, "101", "Frankfurt");
        producer.log(TOPIC_LOCATIONS, "101", "Köln");
        producer.log(TOPIC_LOCATIONS, "102", "Wiesbaden");
        producer.log(TOPIC_LOCATIONS, "102", "Mainz");
        producer.log(TOPIC_LOCATIONS, "102", "Heidelberg");
        producer.log(TOPIC_LOCATIONS, "103", "Berlin");
        producer.log(TOPIC_LOCATIONS, "103", "Hamburg");
        producer.log(TOPIC_LOCATIONS, "103", "München");

        final StreamSettings<String, String> settings = StreamSettings.usingDefaults(INSTANCE_ID);

        final KStreamBuilder builder = new KStreamBuilder();

        final KStream<String, String> customerStream = builder.stream(TOPIC_CUSTOMERS);
        final KStream<String, String> locationsStream = builder.stream(TOPIC_LOCATIONS);

        customerStream
                .join(locationsStream, (abo, location) -> String.format("%s travelled to %s", abo, location), JoinWindows.of(1000))
                .foreach((k, v) -> log.info(String.format("%s -> %s", k, v)));

        final KafkaStreams streams = new KafkaStreams(builder, settings.getProperties());

        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
