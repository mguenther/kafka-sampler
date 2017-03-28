package net.mguenther.kafkasampler.streams.advertisements;

import lombok.RequiredArgsConstructor;
import net.mguenther.kafkasampler.adapter.kafka.JsonCodec;
import net.mguenther.kafkasampler.adapter.kafka.Producer;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class ClickGenerator {

    public static void main(String[] args) throws Exception {
        //bootstrapAdvertisements();
        generateClickEvents();
    }

    private static void bootstrapAdvertisements() {
        final Producer<Advertisement, String> producer = new Producer<>(Naming.PRODUCER_ID, ProducerSettings.usingDefaults(new JsonCodec<>(Advertisement.class)));
        ADVERTISEMENTS.forEach(advertisement -> producer.log(Naming.TOPIC_AD_DETAILS, advertisement.getAdvertisementId(), advertisement));
    }

    private static void generateClickEvents() throws Exception {
        final Producer<AdvertisementClicked, String> producer = new Producer<>(Naming.PRODUCER_ID, ProducerSettings.usingDefaults(new JsonCodec<>(AdvertisementClicked.class)));
        final PoissonBirthProcess arrivals = new PoissonBirthProcess(3.0f);
        while (true) {

            final AdvertisementClicked event = new AdvertisementClicked(
                    oneOf(ADVERTISEMENTS).getAdvertisementId(),
                    oneOf(REFERRER));
            producer.log(Naming.TOPIC_AD_CLICKS, event.getAdvertisementId(), event);
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
