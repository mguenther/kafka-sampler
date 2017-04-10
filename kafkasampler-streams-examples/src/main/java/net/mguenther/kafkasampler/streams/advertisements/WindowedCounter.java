package net.mguenther.kafkasampler.streams.advertisements;

import net.mguenther.kafkasampler.adapter.kafka.StreamSettings;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class WindowedCounter {

    public static void main(String[] args) {

        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> eventStream = builder.stream(Serdes.String(), Serdes.String(), Naming.TOPIC_AD_CLICKS);
        KGroupedStream<String, AdvertisementClicked> clicksPerAdvertisement = eventStream
                .mapValues(AdvertisementClicked::fromJson)
                .groupByKey();
        KTable<Windowed<String>, Long> clicksPerMinute = clicksPerAdvertisement.count(TimeWindows.of(5000), "clicksPerMinute");
        clicksPerMinute
                .toStream((wk, v) -> wk.key())
                .to(Serdes.String(), Serdes.Long(), Naming.TOPIC_AD_CLICKS_PER_MINUTE);

        StreamSettings<String, Long> settings = StreamSettings.builder(Naming.INSTANCE_ID, Serdes.String(), Serdes.Long()).usingTimestampExtractor(EventTimeTimestampExtractor.class).build();
        KafkaStreams topology = new KafkaStreams(builder, settings.getProperties());

        topology.start();

        Runtime.getRuntime().addShutdownHook(new Thread(topology::close));
    }
}
