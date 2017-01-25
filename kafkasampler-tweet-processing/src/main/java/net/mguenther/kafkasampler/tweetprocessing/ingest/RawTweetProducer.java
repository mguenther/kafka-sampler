package net.mguenther.kafkasampler.tweetprocessing.ingest;

import net.mguenther.kafkasampler.adapter.kafka.Producer;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import twitter4j.Status;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class RawTweetProducer extends Producer<Status, String> {

    public RawTweetProducer(final String producerId,
                            final ProducerSettings<Status, String> producerSettings) {
        super(producerId, producerSettings);
    }
}
