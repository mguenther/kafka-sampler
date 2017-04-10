package net.mguenther.kafkasampler.streams.advertisements;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class EventTimeTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(final ConsumerRecord<Object, Object> record) {

        final String clickEventAsJson = (String) record.value();
        try {
            final AdvertisementClicked event = AdvertisementClicked.fromJson(clickEventAsJson);
            return event.getTimestamp();
        } catch (Exception e) {
            // fallback to wall-time
            log.info("Unable to extract event-time from record {}. Falling back to wall-time.", clickEventAsJson);
            return System.currentTimeMillis();
        }
    }
}
