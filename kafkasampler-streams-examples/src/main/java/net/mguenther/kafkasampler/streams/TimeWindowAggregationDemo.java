package net.mguenther.kafkasampler.streams;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafkasampler.adapter.kafka.JsonCodec;
import net.mguenther.kafkasampler.adapter.kafka.Producer;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import net.mguenther.kafkasampler.adapter.kafka.StreamSettings;
import net.mguenther.kafkasampler.adapter.kafka.StringIdentityCodec;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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

    public static void main(String[] args) throws Exception {

        bootstrapAdvertisements();

        final KStreamBuilder builder = new KStreamBuilder();
        final KStream<String, String> advertisementClickedStream = builder.stream(Serdes.String(), Serdes.String(), TOPIC_AD_CLICKS);

        KGroupedStream<String, AdvertisementClicked> clicksPerAdvertisement = advertisementClickedStream
                .mapValues(AdvertisementClicked::fromJson)
                .groupByKey();

        //KTable<String, Long> totalNumberOfClicks = clicksPerAdvertisement.count("totalNumberOfClicksPerAdvertisement");

        //KStream<String, String> advertisementStream = builder.stream(Serdes.String(), Serdes.String(), TOPIC_AD_DETAILS);
        //advertisementStream.mapValues(Advertisement::fromJson);
        KTable<Windowed<String>, Long> clicksPerMinute = clicksPerAdvertisement.count(TimeWindows.of(5000), "clicksPerMinute");
        clicksPerMinute
                .toStream((wk, v) -> wk.key())
                .mapValues(count -> String.valueOf(count))
                .to(Serdes.String(), Serdes.String(), "ads-minute-by-minute-clicks");

        KStream<String, String> minuteByMinuteClicks = builder.stream("ads-minute-by-minute-clicks");
        minuteByMinuteClicks.foreach((k, v) -> log.info("Advertisement with ID {} has received {} clicks during the last minute.", k, v));

        //clicksPerMinute.foreach((w, count) -> log.info("Advertisement with ID {} has received {} clicks during the last minute."));

        //clicksPerMinute.join



        final StreamSettings<String, String> settings = StreamSettings.usingDefaults(INSTANCE_ID);
        final KafkaStreams topology = new KafkaStreams(builder, settings.getProperties());
        topology.start();

        Runtime.getRuntime().addShutdownHook(new Thread(topology::close));

        generateClickEvents(); // runs ad infinitum
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

    @Getter
    @ToString
    static class AdvertisementClicked {

        private static final ObjectMapper MAPPER = new ObjectMapper();

        @JsonProperty("advertisementId")
        private final String advertisementId;

        @JsonProperty("referrer")
        private final String referrer;

        @JsonProperty("timestamp")
        private final long timestamp;

        private AdvertisementClicked(final String advertisementId, final String referrer) {
            this.advertisementId = advertisementId;
            this.referrer = referrer;
            this.timestamp = Instant.now(Clock.systemUTC()).toEpochMilli();
        }

        @JsonCreator
        public AdvertisementClicked(@JsonProperty("advertisementId") final String advertisementId,
                                    @JsonProperty("referrer") final String referrer,
                                    @JsonProperty("timestamp") final long timestamp) {
            this.advertisementId = advertisementId;
            this.referrer = referrer;
            this.timestamp = timestamp;
        }

        public String toJson() {
            try {
                return MAPPER.writeValueAsString(this);
            } catch (IOException e) {
                throw new RuntimeException("Unable to serialize instance of AdvertisementClicked to JSON string.", e);
            }
        }

        public static AdvertisementClicked fromJson(final String json) {
            try {
                return MAPPER.readValue(json, AdvertisementClicked.class);
            } catch (IOException e) {
                throw new RuntimeException("Unable to deserialize JSON string to instance of AdvertisementClicked.", e);
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    static class Advertisement {

        private static final ObjectMapper MAPPER = new ObjectMapper();
        private final String advertisementId;
        private final String description;

        public String toJson() {
            try {
                return MAPPER.writeValueAsString(this);
            } catch (IOException e) {
                throw new RuntimeException("Unable to serialize instance of Advertisement to JSON string.", e);
            }
        }

        public static Advertisement fromJson(final String json) {
            try {
                return MAPPER.readValue(json, Advertisement.class);
            } catch (IOException e) {
                throw new RuntimeException("Unable to deserialize JSON string to instance of Advertisement.", e);
            }
        }
    }

    @RequiredArgsConstructor
    static class MinuteByMinuteReport {

        private static final ObjectMapper MAPPER = new ObjectMapper();
        private final String advertisementId;
        private final String description;
        private final int numberOfClicks;

        public String toJson() {
            try {
                return MAPPER.writeValueAsString(this);
            } catch (IOException e) {
                throw new RuntimeException("Unable to serialize instance of MinuteByMinuteReport to JSON string.", e);
            }
        }

        public static MinuteByMinuteReport fromJson(final String json) {
            try {
                return MAPPER.readValue(json, MinuteByMinuteReport.class);
            } catch (IOException e) {
                throw new RuntimeException("Unable to deserialize JSON string to instance of MinuteByMinuteReport.", e);
            }
        }
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
