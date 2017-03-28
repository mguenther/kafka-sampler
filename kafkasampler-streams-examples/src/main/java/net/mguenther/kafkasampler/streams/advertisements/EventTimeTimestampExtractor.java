package net.mguenther.kafkasampler.streams.advertisements;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class EventTimeTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(final ConsumerRecord<Object, Object> record) {

        final String clickEventAsJson = (String) record.value();
        try {
            final AdvertisementClicked event = AdvertisementClicked.fromJson(clickEventAsJson);
            return event.getTimestamp();
        } catch (Exception e) {
            // fallback to wall-time
            return System.currentTimeMillis();
        }
    }
}
