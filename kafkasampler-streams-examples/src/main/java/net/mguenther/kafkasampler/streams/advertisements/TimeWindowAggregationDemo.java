package net.mguenther.kafkasampler.streams.advertisements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafkasampler.adapter.kafka.JsonCodec;
import net.mguenther.kafkasampler.adapter.kafka.Producer;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import net.mguenther.kafkasampler.adapter.kafka.StreamSettings;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class TimeWindowAggregationDemo {

    private static final String INSTANCE_ID = UUID.randomUUID().toString().substring(0, 4);

    private static final String PRODUCER_ID = String.format("producer-%s", INSTANCE_ID);

    private static final String TOPIC_AD_CLICKS = String.format("ads-clicks-%s", INSTANCE_ID);

    private static final String TOPIC_AD_DETAILS = String.format("ads-details-%s", INSTANCE_ID);

    private static final String TOPIC_AD_REPORTS = String.format("ads-minute-by-minute-clicks-%s", INSTANCE_ID);

    public static void main(String[] args) throws Exception {

        bootstrapAdvertisements();

        final KStreamBuilder builder = new KStreamBuilder();
        final KStream<String, String> advertisementClickedStream = builder.stream(Serdes.String(), Serdes.String(), TOPIC_AD_CLICKS);

        KGroupedStream<String, AdvertisementClicked> clicksPerAdvertisement = advertisementClickedStream
                .mapValues(AdvertisementClicked::fromJson)
                .groupByKey();
        KTable<String, Advertisement> advertisements = builder
                .table(Serdes.String(), Serdes.String(), TOPIC_AD_DETAILS, "advertisements")
                .mapValues(Advertisement::fromJson);
        KTable<Windowed<String>, Long> clicksPerMinute = clicksPerAdvertisement.count(TimeWindows.of(60 * 1000), "clicksPerMinute");
        clicksPerMinute
                .toStream((wk, v) -> wk.key())
                .mapValues(String::valueOf)
                .leftJoin(advertisements, (count, advertisement) -> new MinuteByMinuteReport(advertisement, Long.valueOf(count)))
                .mapValues(MinuteByMinuteReport::toJson)
                .to(Serdes.String(), Serdes.String(), TOPIC_AD_REPORTS);

        KStream<String, MinuteByMinuteReport> minuteByMinuteClicks = builder
                .stream(Serdes.String(), Serdes.String(), TOPIC_AD_REPORTS)
                .mapValues(MinuteByMinuteReport::fromJson);
        minuteByMinuteClicks.foreach((k, v) -> log.info("Advertisement '{}' has received {} clicks during the last minute.", v.getDescription(), v.getNumberOfClicks()));

        final StreamSettings<String, String> settings = StreamSettings.usingDefaults(INSTANCE_ID);
        final KafkaStreams topology = new KafkaStreams(builder, settings.getProperties());
        topology.start();

        Runtime.getRuntime().addShutdownHook(new Thread(topology::close));

        generateClickEvents();
    }

    private static void bootstrapAdvertisements() {
        final Producer<Advertisement, String> producer = new Producer<>(PRODUCER_ID, ProducerSettings.usingDefaults(new JsonCodec<>(Advertisement.class)));
        ADVERTISEMENTS.forEach(advertisement -> producer.log(TOPIC_AD_DETAILS, advertisement.getAdvertisementId(), advertisement));
    }

    private static void generateClickEvents() throws Exception {
        final Producer<AdvertisementClicked, String> producer = new Producer<>(PRODUCER_ID, ProducerSettings.usingDefaults(new JsonCodec<>(AdvertisementClicked.class)));
        final PoissonBirthProcess arrivals = new PoissonBirthProcess(3.0f);
        while (true) {

            final AdvertisementClicked event = new AdvertisementClicked(
                    oneOf(ADVERTISEMENTS).getAdvertisementId(),
                    oneOf(REFERRER));
            producer.log(TOPIC_AD_CLICKS, event.getAdvertisementId(), event);
            Thread.sleep(arrivals.nextInterarrivalTime());
        }
    }

    private static final List<Advertisement> ADVERTISEMENTS = new ArrayList<>();
    private static final List<String> REFERRER = new ArrayList<>();

    static {
        ADVERTISEMENTS.add(new Advertisement("0", "Exxt - Wir vermieten Ihnen Autos!"));
        ADVERTISEMENTS.add(new Advertisement("1", "MucusWeg - Günstiger Hustensaft!"));
        ADVERTISEMENTS.add(new Advertisement("2", "Goldankauf online - Zuverlässig und sicher"));

        REFERRER.add("www.abc.de");
        REFERRER.add("www.online-shop.de");
        REFERRER.add("www.tagesnachrichten-aktuell.de");
        REFERRER.add("www.random-ecommerce-site.biz");
        REFERRER.add("www.my-personal-homepage.co.uk");
    }

    private static <T> T oneOf(final List<T> listOfThings) {
        final int index = (int) (Math.random() * listOfThings.size());
        return listOfThings.get(index);
    }

    @RequiredArgsConstructor
    static class PoissonBirthProcess {

        private final float ratePerSecond;

        public int nextInterarrivalTime() {
            return (int) (poisson() * 1000);
        }

        private float poisson() {
            // 1.0 => events in 1 second
            return -1.0f / ratePerSecond * (float) Math.log(Math.random());
        }
    }
}
