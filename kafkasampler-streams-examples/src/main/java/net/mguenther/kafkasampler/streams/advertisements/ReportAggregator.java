package net.mguenther.kafkasampler.streams.advertisements;

import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafkasampler.adapter.kafka.JsonCodec;
import net.mguenther.kafkasampler.adapter.kafka.Producer;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import net.mguenther.kafkasampler.adapter.kafka.StreamSettings;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.ArrayList;
import java.util.List;

/**
 * !!! Not working, due to join-time-semantics on the windowed-count-stream !!!
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class ReportAggregator {


    private static final List<Advertisement> ADVERTISEMENTS = new ArrayList<>();

    static {
        ADVERTISEMENTS.add(new Advertisement("0", "Exxt - Wir vermieten Ihnen Autos!"));
        ADVERTISEMENTS.add(new Advertisement("1", "MucusWeg - Günstiger Hustensaft!"));
        ADVERTISEMENTS.add(new Advertisement("2", "Goldankauf online - Zuverlässig und sicher"));
    }

    private static void bootstrapAdvertisements() {
        final Producer<Advertisement, String> producer = new Producer<>(Naming.PRODUCER_ID, ProducerSettings.usingDefaults(new JsonCodec<>(Advertisement.class)));
        ADVERTISEMENTS.forEach(advertisement -> producer.log(Naming.TOPIC_AD_DETAILS, advertisement.getAdvertisementId(), advertisement));
    }

    public static void main(String[] args) throws Exception {

        bootstrapAdvertisements();

        final KStreamBuilder builder = new KStreamBuilder();
        KTable<String, Advertisement> advertisements = builder
                .table(Serdes.String(), Serdes.String(), Naming.TOPIC_AD_DETAILS, "advertisements")
                .mapValues(Advertisement::fromJson);
        KStream<String, Long> windowedCountsStream = builder.stream(Serdes.String(), Serdes.Long(), Naming.TOPIC_AD_CLICKS_PER_MINUTE);
        windowedCountsStream
                .leftJoin(advertisements, (count, advertisement) -> new MinuteByMinuteReport(advertisement, count))
                .mapValues(MinuteByMinuteReport::toJson)
                .to(Serdes.String(), Serdes.String(), Naming.TOPIC_AD_REPORTS);

        KStream<String, MinuteByMinuteReport> minuteByMinuteClicks = builder
                .stream(Serdes.String(), Serdes.String(), Naming.TOPIC_AD_REPORTS)
                .mapValues(MinuteByMinuteReport::fromJson);
        minuteByMinuteClicks.foreach((k, v) -> log.info("Advertisement '{}' has received {} clicks during the last minute.", v.getDescription(), v.getNumberOfClicks()));

        final StreamSettings<String, String> settings = StreamSettings.usingDefaults(Naming.INSTANCE_ID);
        final KafkaStreams topology = new KafkaStreams(builder, settings.getProperties());

        topology.start();

        Runtime.getRuntime().addShutdownHook(new Thread(topology::close));
    }
}
